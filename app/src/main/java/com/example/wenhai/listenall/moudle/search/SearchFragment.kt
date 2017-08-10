package com.example.wenhai.listenall.moudle.search

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.moudle.main.MainActivity
import com.example.wenhai.listenall.moudle.main.MainFragment

class SearchFragment : Fragment(), SearchContract.View {

    @BindView(R.id.search_begin_search)
    lateinit var mTvBeginSearch: TextView
    @BindView(R.id.hot_search)
    lateinit var mHotSearch: LinearLayout
    @BindView(R.id.search_content_list)
    lateinit var mContentList: RecyclerView

    lateinit var mUnBinder: Unbinder
    lateinit var mPresenter: SearchContract.Presenter
    var searchKeyword = ""
    lateinit var resultSongs: List<Song>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SearchPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_search, container, false)
        mUnBinder = ButterKnife.bind(this, contentView)
        return contentView
    }

    override fun setPresenter(presenter: SearchContract.Presenter) {
        mPresenter = presenter
    }

    override fun initView() {

    }

    override fun onSearchResult(songs: List<Song>) {
        //显示歌曲
        activity.runOnUiThread {
            mTvBeginSearch.visibility = View.GONE
            val mainFragment: MainFragment = fragmentManager.findFragmentById(R.id.main_container) as MainFragment
            mainFragment.hideSoftInput()
            resultSongs = songs
            mContentList.adapter = ResultSongsAdapter(context, resultSongs)
            mContentList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    @OnClick(R.id.search_begin_search)
    fun onClick(view: View) {
        when (view.id) {
            R.id.search_begin_search -> {
                if (! TextUtils.isEmpty(searchKeyword)) {
                    //begin search
                    beginSearch(searchKeyword)
                }

            }
        }
    }

    private fun beginSearch(keyword: String) {
        mPresenter.searchByKeyWord(keyword)

    }

    fun showSearchHistory() {
        mHotSearch.visibility = View.VISIBLE
        mTvBeginSearch.visibility = View.GONE
        mContentList.adapter = null
        // TODO: 2017/8/10 显示搜索历史

    }

    fun showSearchRecommend(keyword: String) {
        searchKeyword = keyword
        mHotSearch.visibility = View.GONE
        mTvBeginSearch.visibility = View.VISIBLE
        val display = "搜索\"$keyword\""
        mTvBeginSearch.text = display
        mContentList.adapter = null
        mPresenter.loadSearchRecommend(searchKeyword)
    }

    override fun onSearchRecommendLoaded(recommends: List<String>) {
        activity.runOnUiThread {
            mContentList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mContentList.adapter = SearchRecommendAdapter(recommends)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val mainFragment: MainFragment = fragmentManager.findFragmentById(R.id.main_container) as MainFragment
        mainFragment.hideSearchBar()
        mUnBinder.unbind()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class ResultSongsAdapter(val context: Context, var songs: List<Song>) : RecyclerView.Adapter<ResultSongsAdapter.ViewHolder>() {
        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val song = songs[position]
            if (holder != null) {
                holder.songName.text = song.name
                val displayArtist = "${song.artistName}-${song.albumName}"
                holder.artist.text = displayArtist

                holder.btnMore.setOnClickListener {

                }

                holder.item.setOnClickListener {
                    (activity as MainActivity).playNewSong(song)
                }
            }
        }

        override fun getItemCount(): Int = songs.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false)
            return ViewHolder(itemView)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val songName: TextView = itemView.findViewById(R.id.result_song_name)
            val artist: TextView = itemView.findViewById(R.id.result_artist_album)
            val btnMore: ImageButton = itemView.findViewById(R.id.result_more)
            val item: LinearLayout = itemView.findViewById(R.id.result_item)
        }
    }

//    class SearchHistoryAdapter()

    inner class SearchRecommendAdapter(var keywords: List<String>) : RecyclerView.Adapter<SearchRecommendAdapter.ViewHolder>() {
        override fun getItemCount(): Int = keywords.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_search_recommend, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val recommendKeyword = keywords[position]
            holder !!.keyword.text = recommendKeyword
            holder.item.setOnClickListener {
                val mainFragment: MainFragment = fragmentManager.findFragmentById(R.id.main_container) as MainFragment
                mainFragment.setSearchKeyword(recommendKeyword)
                beginSearch(recommendKeyword)
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val keyword: TextView = itemView.findViewById(R.id.search_recommend_keyword)
            val item: RelativeLayout = itemView.findViewById(R.id.search_recommend_item)
        }
    }
}