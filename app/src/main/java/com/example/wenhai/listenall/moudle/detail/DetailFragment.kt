package com.example.wenhai.listenall.moudle.detail

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.LikedAlbum
import com.example.wenhai.listenall.data.bean.LikedAlbumDao
import com.example.wenhai.listenall.data.bean.LikedCollect
import com.example.wenhai.listenall.data.bean.LikedCollectDao
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.moudle.main.MainActivity
import com.example.wenhai.listenall.moudle.ranking.RankingContract
import com.example.wenhai.listenall.utils.DAOUtil
import com.example.wenhai.listenall.utils.DateUtil
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.ToastUtil

class DetailFragment : Fragment(), DetailContract.View {
    @BindView(R.id.action_bar_title)
    lateinit var mActionBarTitle: TextView
    @BindView(R.id.detail_cover)
    lateinit var mCover: ImageView
    @BindView(R.id.detail_title)
    lateinit var mTitle: TextView
    @BindView(R.id.detail_artist)
    lateinit var mArtist: TextView
    @BindView(R.id.detail_date)
    lateinit var mDate: TextView
    @BindView(R.id.detail_song_list)
    lateinit var mSongList: RecyclerView
    @BindView(R.id.loading)
    lateinit var mLoading: LinearLayout
    @BindView(R.id.loading_failed)
    lateinit var mLoadFailed: LinearLayout
    @BindView(R.id.detail_liked_icon)
    lateinit var mLikedIcon: ImageView

    private lateinit var mSongListAdapter: SongListAdapter
    lateinit var mPresenter: DetailContract.Presenter
    private lateinit var mUnBinder: Unbinder
    private lateinit var mLoadType: DetailContract.LoadType

    private lateinit var mAlbum: Album
    private lateinit var mCollect: Collect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DetailPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_detail, container, false)
        mUnBinder = ButterKnife.bind(this, contentView)
        mLoadType = arguments.getSerializable(DetailContract.ARGS_LOAD_TYPE) as DetailContract.LoadType
        initView()
        return contentView
    }


    override fun initView() {
        mActionBarTitle.text = when (mLoadType) {
            DetailContract.LoadType.COLLECT -> getString(R.string.collect_detail)
            DetailContract.LoadType.ALBUM -> getString(R.string.album_detail)
            else -> ""
        }
        mSongListAdapter = SongListAdapter(context, ArrayList())
        mSongList.layoutManager = LinearLayoutManager(context)
        mSongList.adapter = mSongListAdapter
        loadDetail()
    }

    private fun loadDetail() {
        when (mLoadType) {
            DetailContract.LoadType.GLOBAL_RANKING -> {
                val ranking: RankingContract.GlobalRanking = arguments.getSerializable(DetailContract.ARGS_GLOBAL_RANKING) as RankingContract.GlobalRanking
                mPresenter.loadGlobalRanking(ranking)
            }
            DetailContract.LoadType.OFFICIAL_RANKING -> {
                val collect: Collect = arguments.getParcelable(DetailContract.ARGS_COLLECT)
                setRankingDetail(collect)
            }
            DetailContract.LoadType.ALBUM -> {
                val id = arguments.getLong(DetailContract.ARGS_ID)
                mPresenter.loadAlbumDetail(id)
            }
            DetailContract.LoadType.COLLECT -> {
                val id = arguments.getLong(DetailContract.ARGS_ID)
                mPresenter.loadCollectDetail(id)
            }
            DetailContract.LoadType.SONG -> {
                val id = arguments.getLong(DetailContract.ARGS_ID)
                mPresenter.loadSongDetail(id)
            }
        }
    }

    private fun playSong(song: Song) {
        (activity as MainActivity).playNewSong(song)
    }

    @OnClick(R.id.action_bar_back, R.id.detail_play_all, R.id.detail_download_all,
            R.id.detail_add_to_play, R.id.detail_liked, R.id.loading_failed)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                FragmentUtil.removeFragment(fragmentManager, this)
            }
            R.id.detail_play_all -> {
                (activity as MainActivity).playService.replaceList(mSongListAdapter.songList)
            }
            R.id.detail_add_to_play -> {
                (activity as MainActivity).playService.addToPlayList(mSongListAdapter.songList)
            }
            R.id.detail_liked -> {
                switchLikedState()
            }
            R.id.detail_download_all -> {
                ToastUtil.showToast(activity, "download all")
            }
            R.id.loading_failed -> {
                loadDetail()
            }
        }
    }

    private fun switchLikedState() {
        if (mLoadType == DetailContract.LoadType.ALBUM) {
            val dao = DAOUtil.getSession(context).likedAlbumDao
            var liked = false
            var likedAlbum = isCurAlbumLiked()
            if (likedAlbum != null) {
                dao.delete(likedAlbum)
                ToastUtil.showToast(activity, "已取消收藏")
            } else {
                likedAlbum = LikedAlbum(mAlbum)
                dao.insert(likedAlbum)
                liked = true
                ToastUtil.showToast(activity, "已收藏")
            }
            setLikedIcon(liked)
        } else if (mLoadType == DetailContract.LoadType.COLLECT) {
            val dao = DAOUtil.getSession(context).likedCollectDao
            var liked = false
            var likedCollect = isCurCollectLiked()
            if (likedCollect != null) {
                dao.delete(likedCollect)
                ToastUtil.showToast(activity, "已取消收藏")
            } else {
                likedCollect = LikedCollect(mCollect)
                dao.insert(likedCollect)
                liked = true
                ToastUtil.showToast(activity, "已收藏")
            }
            setLikedIcon(liked)
        }
    }


    private fun isCurAlbumLiked(): LikedAlbum? {
        var likedAlbum: LikedAlbum? = null
        val dao = DAOUtil.getSession(context).likedAlbumDao
        val list = dao.queryBuilder()
                .where(LikedAlbumDao.Properties.AlbumId.eq(mAlbum.id),
                        LikedAlbumDao.Properties.ProviderName.eq(mAlbum.supplier.name))
                .build().list()
        if (list.size > 0) {
            likedAlbum = list[0]
        }
        return likedAlbum
    }

    private fun isCurCollectLiked(): LikedCollect? {
        var likedCollect: LikedCollect? = null
        val dao = DAOUtil.getSession(context).likedCollectDao
        val list = dao.queryBuilder().where(LikedCollectDao.Properties.CollectId.eq(mCollect.id),
                LikedCollectDao.Properties.ProviderName.eq(mCollect.source.name))
                .build()
                .list()
        if (list.size > 0) {
            likedCollect = list[0]
        }
        return likedCollect
    }


    private fun setLikedIcon(isLiked: Boolean) {
        if (isLiked) {
            mLikedIcon.setImageResource(R.drawable.ic_liked)
        } else {
            mLikedIcon.setImageResource(R.drawable.ic_like_border)
        }
    }

    override fun setPresenter(presenter: DetailContract.Presenter) {
        mPresenter = presenter
    }

    override fun onLoading() {
        mLoading.visibility = View.VISIBLE
        mLoadFailed.visibility = View.GONE
        mSongList.visibility = View.GONE
    }

    override fun onCollectDetailLoad(collect: Collect) {
        activity.runOnUiThread({
            mCollect = collect
            mTitle.text = collect.title
            mArtist.visibility = View.GONE
            GlideApp.with(context).load(collect.coverUrl)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(mCover)
            val displayDate = "更新时间：${DateUtil.getDate(collect.updateDate)}"
            mDate.text = displayDate
            mSongListAdapter.setData(collect.songs)

            mLoading.visibility = View.GONE
            mSongList.visibility = View.VISIBLE

            if (isCurCollectLiked() != null) {
                setLikedIcon(true)
            }
        })
    }

    private fun setRankingDetail(collect: Collect) {
        activity.runOnUiThread({
            mActionBarTitle.text = collect.title
            mTitle.text = collect.title
            mArtist.text = collect.desc
            GlideApp.with(context)
                    .load(collect.coverDrawable)
                    .into(mCover)
            mDate.visibility = View.GONE
            mSongListAdapter.setData(collect.songs)

            mLoading.visibility = View.GONE
            mSongList.visibility = View.VISIBLE
        })
    }

    override fun onGlobalRankingLoad(collect: Collect) {
        activity.runOnUiThread {
            setRankingDetail(collect)
        }
    }


    override fun onAlbumDetailLoad(album: Album) {
        activity.runOnUiThread {
            mAlbum = album
            mTitle.text = album.title
            mArtist.visibility = View.VISIBLE
            mArtist.text = album.artist
            val displayDate = "发行时间：${DateUtil.getDate(album.publishDate)}"
            GlideApp.with(context).load(album.coverUrl)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(mCover)
            mDate.text = displayDate
            mSongListAdapter.setData(album.songs)

            mLoading.visibility = View.GONE
            mSongList.visibility = View.VISIBLE
            if (isCurAlbumLiked() != null) {
                setLikedIcon(true)
            }
        }

    }

    override fun onFailure(msg: String) {
        activity.runOnUiThread {
            mLoading.visibility = View.GONE
            mLoadFailed.visibility = View.VISIBLE
            ToastUtil.showToast(context, msg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    companion object {
        const val TAG = "DetailFragment"
    }

    inner class SongListAdapter(val context: Context, var songList: List<Song>) : RecyclerView.Adapter<SongListAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val song = songList[position]
            val index = "${position + 1}"
            holder !!.index.text = index
            holder.title.text = song.name
            val displayArtistName =
                    if (mLoadType == DetailContract.LoadType.ALBUM) {
                        song.artistName
                    } else {
                        val artistAlbum = "${song.artistName} · ${song.albumName}"
                        if (artistAlbum.length < 30) {
                            artistAlbum
                        } else {
                            song.artistName
                        }
                    }
            holder.artistAlbum.text = displayArtistName
            holder.item.setOnClickListener({
                playSong(song)
            })
        }

        override fun getItemCount(): Int = songList.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_detail_song_list, parent, false)
            return ViewHolder(itemView)
        }

        fun setData(songList: List<Song>) {
            this.songList = songList
            notifyDataSetChanged()
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var item: LinearLayout = itemView.findViewById(R.id.detail_song_item)
            var index: TextView = itemView.findViewById(R.id.detail_index)
            val title: TextView = itemView.findViewById(R.id.detail_song_title)
            var artistAlbum: TextView = itemView.findViewById(R.id.detail_artist_album)
        }
    }
}