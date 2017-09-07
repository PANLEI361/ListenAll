package com.example.wenhai.listenall.moudle.artist.detail

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Artist
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.ktextension.hide
import com.example.wenhai.listenall.ktextension.isShowing
import com.example.wenhai.listenall.ktextension.show
import com.example.wenhai.listenall.ktextension.showToast
import com.example.wenhai.listenall.moudle.detail.DetailContract
import com.example.wenhai.listenall.moudle.detail.DetailFragment
import com.example.wenhai.listenall.moudle.main.MainActivity
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.scwang.smartrefresh.layout.SmartRefreshLayout

class ArtistDetailFragment : Fragment(), ArtistDetailContract.View {

    @BindView(R.id.detail_pager)
    lateinit var mPager: ViewPager
    @BindView(R.id.detail_pager_tab)
    lateinit var mTab: TabLayout
    @BindView(R.id.detail_artist_name)
    lateinit var mArtistName: TextView
    @BindView(R.id.detail_artist_photo)
    lateinit var mArtistPhoto: ImageView


    //歌手热门歌曲
    lateinit var mHotSongsView: LinearLayout
    private lateinit var mHotSongList: RecyclerView
    private lateinit var mHotSongRefresh: SmartRefreshLayout
    lateinit var mShuffleAll: LinearLayout
    private lateinit var mHotSongAdapter: HotSongsAdapter
    private var curHotSongPage = 1


    //歌手专辑
    lateinit var mAlbumsView: LinearLayout
    private lateinit var mAlbumList: RecyclerView
    private lateinit var mAlbumRefresh: SmartRefreshLayout
    private lateinit var mAlbumAdapter: AlbumAdapter
    private var curAlbumPage = 1

    //歌手详情
    lateinit var mArtistInfoView: LinearLayout
    private lateinit var mArtistDesc: TextView

    private lateinit var mUnbinder: Unbinder
    lateinit var mPresenter: ArtistDetailContract.Presenter
    lateinit var artist: Artist


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArtistDetailPresenter(this)
        artist = arguments.getParcelable("artist")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_artist_detail, container, false)

        mHotSongsView = inflater.inflate(R.layout.fragment_artist_detail_hot_songs, container, false) as LinearLayout
        mHotSongList = mHotSongsView.findViewById(R.id.detail_song_list)
        mHotSongRefresh = mHotSongsView.findViewById(R.id.hotSongRefresh)
        mShuffleAll = mHotSongsView.findViewById(R.id.shuffle_all)

        mAlbumsView = inflater.inflate(R.layout.fragment_artist_detail_albums, container, false) as LinearLayout
        mAlbumList = mAlbumsView.findViewById(R.id.detail_album_list)
        mAlbumRefresh = mAlbumsView.findViewById(R.id.album_refresh)

        mArtistInfoView = inflater.inflate(R.layout.fragment_artist_detail_info, container, false) as LinearLayout
        mArtistDesc = mArtistInfoView.findViewById(R.id.detail_artist_desc)
        mUnbinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    override fun initView() {
        mPager.adapter = DetailPagerAdapter()
        mTab.setupWithViewPager(mPager)
        mPresenter.loadArtistDetail(artist)
        mArtistName.text = artist.name
        mArtistPhoto.setOnClickListener { }

        mPresenter.loadArtistHotSongs(artist, curHotSongPage)
        mPresenter.loadArtistAlbums(artist, curAlbumPage)

        initHotSongView()
        initAlbumView()

    }

    private fun initHotSongView() {
        mHotSongAdapter = HotSongsAdapter(context, ArrayList())
        mHotSongList.layoutManager = LinearLayoutManager(context)
        mHotSongList.adapter = mHotSongAdapter
        mHotSongRefresh.setOnLoadmoreListener {
            mPresenter.loadArtistHotSongs(artist, curHotSongPage)
        }
        mShuffleAll.setOnClickListener {
            (activity as MainActivity).playService.shuffleAll(mHotSongAdapter.hotSongs)
        }
    }

    private fun initAlbumView() {
        mAlbumAdapter = AlbumAdapter(context, ArrayList())
        mAlbumList.adapter = mAlbumAdapter
        mAlbumList.layoutManager = LinearLayoutManager(context)
        mAlbumRefresh.setOnLoadmoreListener {
            mPresenter.loadArtistAlbums(artist, curAlbumPage)
        }
    }

    @OnClick(R.id.action_bar_back)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                FragmentUtil.removeFragment(fragmentManager, this)
            }
        }
    }


    override fun setPresenter(presenter: ArtistDetailContract.Presenter) {
        mPresenter = presenter
    }

    override fun onFailure(msg: String) {
        activity.runOnUiThread {
            if (mAlbumRefresh.isLoading) {
                mAlbumRefresh.finishLoadmore(200, false)
            }
            if (mHotSongRefresh.isLoading) {
                mHotSongRefresh.finishLoadmore(200, false)
            }
            context.showToast(msg)
        }
    }

    override fun onLoading() {
    }

    override fun onArtistDetail(artist: Artist) {
        activity.runOnUiThread {
            GlideApp.with(this).load(artist.imgUrl)
                    .placeholder(R.drawable.ic_main_singer)
                    .into(mArtistPhoto)
            mArtistDesc.text = artist.desc
        }
    }

    override fun onHotSongsLoad(hotSongs: List<Song>) {
        activity.runOnUiThread {
            if (mHotSongRefresh.isLoading) {
                mHotSongRefresh.finishLoadmore(200, true)
            }
            if (! mShuffleAll.isShowing()) {
                mShuffleAll.show()
            }
            curHotSongPage ++
            mHotSongAdapter.addData(hotSongs)
        }
    }

    override fun onAlbumsLoad(albums: List<Album>) {
        activity.runOnUiThread {
            if (mAlbumRefresh.isLoading) {
                mAlbumRefresh.finishLoadmore(200, true)
            }
            curAlbumPage ++
            mAlbumAdapter.addData(albums)
        }
    }

    override fun onSongDetailLoaded(song: Song) {
        (activity as MainActivity).playNewSong(song)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }


    inner class DetailPagerAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val pageView = when (position) {
                0 -> {
                    mHotSongsView
                }
                1 -> {
                    mAlbumsView
                }
                2 -> {
                    mArtistInfoView
                }
                else -> {
                    mHotSongsView
                }
            }
            container !!.addView(pageView, position)
            return pageView
        }

        override fun getItemPosition(`object`: Any?): Int {
            return super.getItemPosition(`object`)
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container !!.removeViewAt(position)
//            super.destroyItem(container, position, `object`)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> {
                    "热门歌曲"
                }
                1 -> {
                    "专辑"
                }
                2 -> {
                    "艺人详情"
                }
                else -> {
                    ""
                }
            }
        }

        override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

        override fun getCount(): Int = 3

    }

    inner class HotSongsAdapter(val context: Context, var hotSongs: List<Song>) : RecyclerView.Adapter<HotSongsAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val song = hotSongs[position]
            val index = "${position + 1}"
            holder !!.index.text = index
            holder.title.text = song.name
            // 虾米没有专辑信息
            if (song.albumName != "") {
                holder.album.hide()
                holder.album.text = song.albumName
            } else {
                holder.album.hide()
            }
            holder.item.setOnClickListener({
                mPresenter.loadSongDetail(song)
            })
        }

        override fun getItemCount(): Int = hotSongs.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_artist_detail_song_list, parent, false)
            return ViewHolder(itemView)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var item: LinearLayout = itemView.findViewById(R.id.detail_song_item)
            var index: TextView = itemView.findViewById(R.id.detail_index)
            val title: TextView = itemView.findViewById(R.id.detail_song_title)
            var album: TextView = itemView.findViewById(R.id.detail_album)
        }

        fun addData(data: List<Song>) {
            (hotSongs as ArrayList).addAll(data)
            notifyDataSetChanged()
        }
    }

    inner class AlbumAdapter(val context: Context, private var albums: List<Album>) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_artist_detail_album, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val album = albums[position]
            GlideApp.with(context).load(album.miniCoverUrl)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(holder !!.albumCover)
            holder.albumName.text = album.title
            val publishDate = "发行时间:${album.publishDateStr}"
            holder.albumPublishDate.text = publishDate
            holder.item.setOnClickListener {
                val detailFragment = DetailFragment()
                val data = Bundle()
                data.putLong(DetailContract.ARGS_ID, album.id)
                data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.ALBUM)
                detailFragment.arguments = data
                FragmentUtil.addFragmentToMainView(fragmentManager, detailFragment)

            }
        }

        fun addData(data: List<Album>) {
            (albums as ArrayList).addAll(data)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = albums.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val albumCover: ImageView = itemView.findViewById(R.id.detail_album_cover)
            val albumName: TextView = itemView.findViewById(R.id.detail_album_name)
            val albumPublishDate: TextView = itemView.findViewById(R.id.detail_album_publish_date)
            val item: LinearLayout = itemView.findViewById(R.id.detail_album_item)
        }
    }

}