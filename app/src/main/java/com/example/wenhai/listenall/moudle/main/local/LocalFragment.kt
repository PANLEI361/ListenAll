package com.example.wenhai.listenall.moudle.main.local

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.TestSongList
import com.example.wenhai.listenall.moudle.liked.LikedFragment
import com.example.wenhai.listenall.moudle.playhistory.PlayHistoryFragment
import com.example.wenhai.listenall.utils.FragmentUtil


class LocalFragment : android.support.v4.app.Fragment() {
    @BindView(R.id.main_song_list)
    lateinit var mRvSongList: RecyclerView
    @BindView(R.id.main_local_scroll)
    lateinit var mScrollView: ScrollView
    @BindView(R.id.main_local_btn_liked_collect)
    lateinit var mBtnAlbum: Button
    @BindView(R.id.main_local_btn_my_collect)
    lateinit var mBtnCollect: Button
    @BindView(R.id.main_local_btn_liked)
    lateinit var mBtnLiked: ImageButton
    @BindView(R.id.main_local_btn_songs)
    lateinit var mBtnSongs: ImageButton
    @BindView(R.id.main_local_btn_recent_play)
    lateinit var mBtnRecentPlay: ImageButton

    private lateinit var mUnBinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater !!.inflate(R.layout.fragment_main_local, container, false)
        mUnBinder = ButterKnife.bind(this, rootView)
        initView()
        return rootView
    }

    fun initView() {
        mRvSongList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val songList = ArrayList<TestSongList>()
        for (i in 1..10) {
            songList.add(TestSongList("This is a test", R.mipmap.ic_launcher_round))
        }
        val songListAdapter = SongListAdapter(songList, activity)
        mRvSongList.adapter = songListAdapter
    }

    @OnClick(R.id.main_local_btn_songs, R.id.main_local_btn_recent_play, R.id.main_local_btn_liked,
            R.id.main_local_btn_my_collect, R.id.main_local_btn_liked_collect)
    fun onClick(v: View) {
        when (v.id) {
            R.id.main_local_btn_songs -> {
            }
            R.id.main_local_btn_recent_play -> {
                FragmentUtil.addFragmentToMainView(fragmentManager, PlayHistoryFragment())
            }
            R.id.main_local_btn_liked -> {
                FragmentUtil.addFragmentToMainView(fragmentManager, LikedFragment())
            }
            R.id.main_local_btn_my_collect -> {
            }
            R.id.main_local_btn_liked_collect -> {
            }
        }

    }

    override fun onStart() {
        super.onStart()
        mScrollView.smoothScrollTo(0, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    companion object {
        val TAG = "LocalFragment"
    }

    class SongListAdapter(val context: Context) : RecyclerView.Adapter<SongListAdapter.ViewHolder>() {
        private lateinit var songList: List<TestSongList>

        constructor(songList: List<TestSongList>, context: Context) : this(context) {
            this.songList = songList
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.test_item_main_song_list, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            if (holder == null) {
                return
            }
            val testSongList = songList[position]
            val icon = BitmapFactory.decodeResource(context.resources, testSongList.iconId)
            holder.ivIcon.setImageBitmap(icon)
            holder.tvListName.text = testSongList.name
        }

        override fun getItemCount(): Int = songList.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var ivIcon: ImageView = itemView.findViewById(R.id.song_list_icon)
            var tvListName: TextView = itemView.findViewById(R.id.song_list_name)
        }
    }
}



