package com.example.wenhai.listenall.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Song

class PlayListDialog(context: Context, var songList: ArrayList<Song>, themeId: Int) : Dialog(context, themeId) {

    constructor(context: Context, songList: ArrayList<Song>) : this(context, songList, android.R.style.Theme_Holo_Light_Dialog)

    @BindView(R.id.dialog_song_list)
    lateinit var rvSongList: RecyclerView
    @BindView(R.id.dialog_close)
    lateinit var btnClose: Button
    @BindView(R.id.dialog_song_numbers)
    lateinit var tvSongNumbers: TextView

    lateinit var unbinder: Unbinder
    lateinit var adapter: SongListAdapter

    var currentPlayIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_play_list)
        unbinder = ButterKnife.bind(this)
        initView()
    }

    private fun initView() {
        //set style
        val window = window
        window.setGravity(Gravity.BOTTOM)
        window.attributes.width = getScreenWidth()
        tvSongNumbers.text = songList.size.toString()
        rvSongList.layoutManager = LinearLayoutManager(context)
        adapter = SongListAdapter()
        rvSongList.adapter = adapter
    }

    fun setSongs(songList: ArrayList<Song>) {
        this.songList = songList
        tvSongNumbers.text = songList.size.toString()
        adapter.notifyDataSetChanged()
    }

    @OnClick(R.id.dialog_close)
    fun onClick(view: View) {
        when (view.id) {
            R.id.dialog_close -> {
                dismiss()
            }
        }
    }


    private fun getScreenWidth(): Int {
        val manager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(metrics)
        return metrics.widthPixels
    }

    override fun onDetachedFromWindow() {
        unbinder.unbind()
        super.onDetachedFromWindow()
    }

    inner class SongListAdapter : RecyclerView.Adapter<SongListAdapter.ViewHolder>() {
        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val song = songList[position]
            val songInfo = "${song.name} · ${song.artistName}"
            holder !!.songInfo.text = songInfo
            if (song.isPlaying) {
                holder.isPlaying.visibility = View.VISIBLE
            } else {
                holder.isPlaying.visibility = View.GONE
            }

            holder.delete.setOnClickListener {
                // TODO: 2017/8/13 删除
//                songList.r/
            }

        }

        override fun getItemCount(): Int = songList.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_dialog_song_list, parent, false)
            return ViewHolder(itemView)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val songInfo: TextView = itemView.findViewById(R.id.item_song_info)
            val isPlaying: ImageView = itemView.findViewById(R.id.item_isPlaying)
            val delete: ImageButton = itemView.findViewById(R.id.item_delete)
        }
    }

}