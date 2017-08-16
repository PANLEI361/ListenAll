package com.example.wenhai.listenall.moudle.playhistory

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.bean.PlayHistory
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.moudle.main.MainActivity
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.ToastUtil

class PlayHistoryFragment : Fragment(), PlayHistoryContract.View {


    @BindView(R.id.action_bar_title)
    lateinit var mTvTitle: TextView
    @BindView(R.id.play_history_list)
    lateinit var mHistoryList: RecyclerView

    private lateinit var mUnbinder: Unbinder
    private lateinit var mPresenter: PlayHistoryContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayHistoryPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_play_history, container, false)
        mUnbinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    override fun initView() {
        mTvTitle.text = getString(R.string.main_recent_play)
        mPresenter.loadPlayHistory(context)
    }

    @OnClick(R.id.action_bar_back)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                FragmentUtil.removeFragment(fragmentManager, this)
            }
        }
    }

    override fun setPresenter(presenter: PlayHistoryContract.Presenter) {
        mPresenter = presenter
    }

    override fun onPlayHistoryLoad(playHistory: List<PlayHistory>) {
        mHistoryList.layoutManager = LinearLayoutManager(context)
        mHistoryList.adapter = PlayHistoryAdapter(playHistory)
    }

    override fun onNoPlayHistory() = ToastUtil.showToast(context, getString(R.string.no_play_history))

    override fun onFailure(msg: String) {
        ToastUtil.showToast(context, msg)
    }

    inner class PlayHistoryAdapter(private var playHistoryList: List<PlayHistory>) : RecyclerView.Adapter<PlayHistoryAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_play_history, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount(): Int = playHistoryList.size

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val playHistory = playHistoryList[position]
            holder !!.songName.text = playHistory.songName
            holder.playTimes.text = playHistory.playTimes.toString()
            val songInfoStr = "${playHistory.artistName} · ${playHistory.albumName}"
            holder.songInfo.text = songInfoStr
            holder.operation.setOnClickListener {

            }
            holder.item.setOnClickListener {
                val song = Song()
                song.name = playHistory.songName
                song.songId = playHistory.songId
                song.albumId = playHistory.albumId
                song.albumName = playHistory.albumName
                song.artistId = playHistory.artistId
                song.artistName = playHistory.artistName
                song.albumCoverUrl = playHistory.coverUrl
                song.miniAlbumCoverUrl = playHistory.miniAlbumCoverUrl
                song.listenFileUrl = playHistory.listenFileUrl
                song.supplier = when (playHistory.providerName) {
                    MusicProvider.XIAMI.name -> MusicProvider.XIAMI
                    MusicProvider.QQMUSIC.name -> MusicProvider.QQMUSIC
                    MusicProvider.NETEASE.name -> MusicProvider.NETEASE
                    else -> {
                        MusicProvider.XIAMI
                    }
                }
                (activity as MainActivity).playService.playNewSong(song)
            }
        }

        inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
            val item: LinearLayout = item.findViewById(R.id.play_history_item)
            var songName: TextView = item.findViewById(R.id.play_history_song_name)
            var songInfo: TextView = item.findViewById(R.id.play_history_song_info)
            var playTimes: TextView = item.findViewById(R.id.play_history_times)
            var operation: ImageButton = item.findViewById(R.id.play_history_ops)

        }
    }

}