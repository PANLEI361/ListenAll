package com.example.wenhai.listenall.moudle.search

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
import com.example.wenhai.listenall.data.bean.SearchHistory
import com.example.wenhai.listenall.data.bean.SearchHistoryDao
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.extension.hide
import com.example.wenhai.listenall.extension.show
import com.example.wenhai.listenall.moudle.main.MainActivity
import com.example.wenhai.listenall.moudle.main.MainFragment
import com.example.wenhai.listenall.utils.DAOUtil

class SearchFragment : Fragment(), SearchContract.View {

    @BindView(R.id.search_view)
    lateinit var mSearchView: LinearLayout
    @BindView(R.id.search_begin_search)
    lateinit var mTvBeginSearch: TextView
    //    @BindView(R.id.hot_search)
//    lateinit var mHotSearch: LinearLayout
    @BindView(R.id.search_content_list)
    lateinit var mContentList: RecyclerView
    @BindView(R.id.loading)
    lateinit var mLoading: LinearLayout
    @BindView(R.id.loading_failed)
    lateinit var mLoadFailed: LinearLayout

    private lateinit var mUnBinder: Unbinder
    lateinit var mPresenter: SearchContract.Presenter
    var searchKeyword = ""
    private lateinit var resultSongs: List<Song>
    private var currentContent = CONTENT_SEARCH_HISTORY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SearchPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_search, container, false)
        mUnBinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    override fun setPresenter(presenter: SearchContract.Presenter) {
        mPresenter = presenter
    }

    override fun initView() {
        showSearchHistory()
    }


    @OnClick(R.id.search_begin_search, R.id.loading_failed)
    fun onClick(view: View) {
        when (view.id) {
            R.id.search_begin_search -> {
                //搜索
                beginSearch(searchKeyword)
            }
            R.id.loading_failed -> {
                //重新搜索
                beginSearch(searchKeyword)
            }
        }
    }

    fun beginSearch(keyword: String) {
        if (! TextUtils.isEmpty(keyword)) {
            val mainFragment: MainFragment = fragmentManager.findFragmentById(R.id.main_container) as MainFragment
            mainFragment.hideSoftInput()
            mPresenter.searchByKeyWord(keyword)
            saveSearchHistory(keyword)
            currentContent = CONTENT_SEARCH_RESULT
        }
    }

    private fun saveSearchHistory(keyword: String) {
        val dao = DAOUtil.getSession(context).searchHistoryDao
        val queryResult = dao.queryBuilder()
                .where(SearchHistoryDao.Properties.Keyword.eq(keyword))
                .list()
        if (queryResult.size > 0) {
            val searchHistory = queryResult[0]
            searchHistory.searchTime = System.currentTimeMillis()
            dao.update(searchHistory)
        } else {
            val newSearch = SearchHistory(null, keyword, System.currentTimeMillis())
            dao.insert(newSearch)
        }
    }

    fun showSearchHistory() {
        currentContent = CONTENT_SEARCH_HISTORY
//        mHotSearch.show()
        mSearchView.hide()
        val dao = DAOUtil.getSession(context).searchHistoryDao
        val query = dao.queryBuilder()
                .where(SearchHistoryDao.Properties.Keyword.notEq(""))
                .orderDesc(SearchHistoryDao.Properties.SearchTime)
                .build()
        val searchHistory = query.list()
        if (mContentList.adapter is SearchHistoryAdapter) {
            (mContentList.adapter as SearchHistoryAdapter).setData(searchHistory)
        } else {
            mContentList.adapter = SearchHistoryAdapter(searchHistory)
            mContentList.layoutManager = LinearLayoutManager(context)
        }
    }

    fun showSearchRecommend(keyword: String) {
        searchKeyword = keyword
        mSearchView.show()
        val display = "搜索\"$keyword\""
        mTvBeginSearch.text = display
        currentContent = CONTENT_RECOMMEND_KEYWORD
        mPresenter.loadSearchRecommend(searchKeyword)
    }

    override fun onSearchRecommendLoaded(recommends: List<String>) {
        if (currentContent == CONTENT_RECOMMEND_KEYWORD) {
            activity.runOnUiThread {
                if (mContentList.adapter is SearchRecommendAdapter) {
                    (mContentList.adapter as SearchRecommendAdapter).setData(recommends)
                } else {
                    mContentList.layoutManager = LinearLayoutManager(context)
                    mContentList.adapter = SearchRecommendAdapter(recommends)
                }
            }
        }
    }

    override fun onSearchResult(songs: List<Song>) {
        if (currentContent == CONTENT_SEARCH_RESULT) {
            activity.runOnUiThread {
                mSearchView.hide()
                resultSongs = songs
                mContentList.adapter = ResultSongsAdapter(resultSongs)
                mContentList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                mLoading.hide()
                mContentList.show()
            }
        }
    }

    override fun onLoading() {
        mLoading.show()
        mContentList.hide()
        mLoadFailed.hide()
    }

    override fun onFailure(msg: String) {
        activity.runOnUiThread {
            mLoadFailed.show()
            mLoading.hide()
            mContentList.hide()
            mSearchView.hide()
//            ToastUtil.showToast(context, msg)
        }
    }

    override fun onSongDetailLoad(song: Song) {
        activity.runOnUiThread {
            (activity as MainActivity).playNewSong(song)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val mainFragment: MainFragment = fragmentManager.findFragmentById(R.id.main_container) as MainFragment
        mainFragment.hideSearchBar()
        mUnBinder.unbind()
    }

    companion object {
        const val CONTENT_SEARCH_HISTORY = 0x00
        const val CONTENT_RECOMMEND_KEYWORD = 0x01
        const val CONTENT_SEARCH_RESULT = 0x02
    }


    inner class ResultSongsAdapter(private var songs: List<Song>) : RecyclerView.Adapter<ResultSongsAdapter.ViewHolder>() {
        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val song = songs[position]
            if (holder != null) {
                holder.songName.text = song.name
                val displayArtist = "${song.artistName}-${song.albumName}"
                holder.artist.text = displayArtist

                holder.btnMore.setOnClickListener {

                }

                holder.item.setOnClickListener {
                    mPresenter.loadSongDetail(song)
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

    inner class SearchHistoryAdapter(private var history: List<SearchHistory>) : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_search_history, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val searchHistory = history[position]
            holder !!.historyKeyword.text = searchHistory.keyword
            holder.deleteHistory.setOnClickListener {
                DAOUtil.getSession(context).searchHistoryDao.delete(searchHistory)
                showSearchHistory()
            }
            holder.item.setOnClickListener {
                searchKeyword = searchHistory.keyword
                val mainFragment: MainFragment = fragmentManager.findFragmentById(R.id.main_container) as MainFragment
                mainFragment.setSearchKeyword(searchHistory.keyword)
                beginSearch(searchHistory.keyword)
            }
        }

        override fun getItemCount(): Int = history.size

        fun setData(newData: List<SearchHistory>) {
            history = newData
            notifyDataSetChanged()
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val historyKeyword: TextView = itemView.findViewById(R.id.search_history_keyword)
            val deleteHistory: ImageButton = itemView.findViewById(R.id.search_delete_history)
            val item: RelativeLayout = itemView.findViewById(R.id.search_history_item)
        }
    }

    inner class SearchRecommendAdapter(private var keywords: List<String>)
        : RecyclerView.Adapter<SearchRecommendAdapter.ViewHolder>() {
        override fun getItemCount(): Int = keywords.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_search_recommend, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val recommendKeyword = keywords[position]
            holder !!.keyword.text = recommendKeyword
            holder.item.setOnClickListener {
                searchKeyword = recommendKeyword
                val mainFragment: MainFragment = fragmentManager.findFragmentById(R.id.main_container) as MainFragment
                mainFragment.setSearchKeyword(recommendKeyword)
                beginSearch(recommendKeyword)
            }
        }

        fun setData(newKeywords: List<String>) {
            keywords = newKeywords
            notifyDataSetChanged()
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val keyword: TextView = itemView.findViewById(R.id.search_recommend_keyword)
            val item: RelativeLayout = itemView.findViewById(R.id.search_recommend_item)
        }
    }
}