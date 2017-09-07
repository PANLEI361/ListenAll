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
import com.example.wenhai.listenall.data.bean.PlayHistory
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.extension.showToast
import com.example.wenhai.listenall.moudle.main.MainActivity
import com.example.wenhai.listenall.utils.FragmentUtil

class PlayHistoryFragment : Fragment(), PlayHistoryContract.View {
    @BindView(R.id.action_bar_title)
    lateinit var mTvTitle: TextView
    @BindView(R.id.play_history_list)
    lateinit var mHistoryList: RecyclerView

    private lateinit var mUnbinder: Unbinder
    private lateinit var mPresenter: PlayHistoryContract.Presenter
    private lateinit var mPlayHistoryAdapter: PlayHistoryAdapter

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
        mPlayHistoryAdapter = PlayHistoryAdapter(ArrayList())
        mHistoryList.layoutManager = LinearLayoutManager(context)
        mHistoryList.adapter = mPlayHistoryAdapter
        mPresenter.loadPlayHistory(context)
    }

    @OnClick(R.id.action_bar_back, R.id.play_history_shuffle_all)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                FragmentUtil.removeFragment(fragmentManager, this)
            }
            R.id.play_history_shuffle_all -> {
                if (mPlayHistoryAdapter.playHistoryList.isEmpty()) {
                    context.showToast(R.string.no_songs_to_play)
                } else {
                    val songList = ArrayList<Song>()
                    mPlayHistoryAdapter.playHistoryList.mapTo(songList) { it.song }
                    (activity as MainActivity).playService.shuffleAll(songList)
                }
            }
        }
    }

    override fun setPresenter(presenter: PlayHistoryContract.Presenter) {
        mPresenter = presenter
    }

    override fun onPlayHistoryLoad(playHistory: List<PlayHistory>) {
        mPlayHistoryAdapter.setData(playHistory)
    }

    override fun onNoPlayHistory() {
        context.showToast(R.string.no_play_history)
    }


    override fun onLoading() {

    }

    override fun onFailure(msg: String) {
        context.showToast(msg)
    }

    inner class PlayHistoryAdapter(var playHistoryList: List<PlayHistory>) : RecyclerView.Adapter<PlayHistoryAdapter.ViewHolder>() {
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
                (activity as MainActivity).playService.playNewSong(playHistory.song)
            }
        }

        fun setData(historyList: List<PlayHistory>) {
            playHistoryList = historyList
            notifyDataSetChanged()
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