package com.example.wenhai.listenall.moudle.detail

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.service.PlayService
import com.example.wenhai.listenall.utils.DateUtil
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.LogUtil

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

    lateinit var mSongListAdapter: SongListAdapter


    companion object {
        const val TAG = "DetailFragment"
    }

    lateinit var mPresenter: DetailContract.Presenter
    lateinit var mUnBinder: Unbinder
    lateinit var mLoadType: Type
    private var mConnection: ServiceConnection? = null
    private var mPlayService: PlayService? = null


    override fun setPresenter(presenter: DetailContract.Presenter) {
        mPresenter = presenter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DetailPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_detail, container, false)
        mUnBinder = ButterKnife.bind(this, contentView)

        val id = arguments.getLong("id")
        val type = arguments.getInt("type")
        mLoadType = if (type == Type.COLLECT.ordinal) {
            Type.COLLECT
        } else {
            Type.ALBUM
        }

        initView()
        mPresenter.loadSongsDetails(id, mLoadType)
        return contentView
    }


    override fun initView() {
        mActionBarTitle.text = if (mLoadType == Type.COLLECT) {
            "歌单详情"
        } else {
            "专辑详情"
        }
        mSongListAdapter = SongListAdapter(context, ArrayList<Song>())
        mSongListAdapter.setOnItemClickListener(object : SongListAdapter.OnItemClickListener {
            override fun onItemClick(song: Song) {
                if (song.listenFileUrl == "") {
                    mPresenter.loadSongDetail(song)
                } else {
                    playSong(song.listenFileUrl)
                }
            }

        })
        mSongList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mSongList.adapter = mSongListAdapter
    }

    private fun playSong(listenFileUrl: String) {
        val intent = Intent(context, PlayService::class.java)
        intent.putExtra("listenUrl", listenFileUrl)
        intent.action = PlayService.ACTION_NEW_SONG
        if (mPlayService == null) {
            mConnection = object : ServiceConnection {
                override fun onServiceDisconnected(p0: ComponentName?) {

                }

                override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                    mPlayService = (binder as PlayService.ControlBinder).getPlayService()
                }

            }
            activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        } else {
            mPlayService !!.playNewSong(listenFileUrl)
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

    override fun onSongDetailLoaded(song: Song) {
        playSong(song.listenFileUrl)
        LogUtil.d(TAG, "获取的地址：${song.listenFileUrl}")

    }

    override fun setCollectDetail(collect: Collect) {
        activity.runOnUiThread({
            mTitle.text = collect.title
            mArtist.visibility = View.GONE
            GlideApp.with(context).load(collect.coverUrl)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(mCover)
            val displayDate = "创建时间：${DateUtil.getDate(collect.createDate)}"
            mDate.text = displayDate
            mSongListAdapter.setData(collect.songs)
        })
    }

    override fun setAlbumDetail(album: Album) {
        activity.runOnUiThread {
            mTitle.text = album.title
            mArtist.visibility = View.VISIBLE
            mArtist.text = album.artist
            val displayDate = "发行时间：${DateUtil.getDate(album.publishDate)}"
            GlideApp.with(context).load(album.coverUrl)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(mCover)
            mDate.text = displayDate
            mSongListAdapter.setData(album.songs)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mConnection != null) {
            activity.unbindService(mConnection)
        }
    }

    class SongListAdapter(val context: Context, var songList: List<Song>) : RecyclerView.Adapter<SongListAdapter.ViewHolder>() {

        lateinit var itemClickListener: OnItemClickListener

        interface OnItemClickListener {
            fun onItemClick(song: Song)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val song = songList[position]
            val index = "${position + 1}"
            holder !!.index.text = index
            holder.title.text = song.name
            val artistName = song.artistName
            holder.artistAlbum.text = artistName
            holder.item.setOnClickListener({
                itemClickListener.onItemClick(song)
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

        fun setOnItemClickListener(listener: OnItemClickListener) {
            itemClickListener = listener
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var item: LinearLayout = itemView.findViewById(R.id.detail_song_item)
            var index: TextView = itemView.findViewById(R.id.detail_index)
            val title: TextView = itemView.findViewById(R.id.detail_song_title)
            var artistAlbum: TextView = itemView.findViewById(R.id.detail_artist_album)
        }
    }
}