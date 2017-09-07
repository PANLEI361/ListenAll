package com.example.wenhai.listenall.moudle.main.local

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
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.moudle.detail.DetailContract
import com.example.wenhai.listenall.moudle.detail.DetailFragment
import com.example.wenhai.listenall.moudle.liked.LikedFragment
import com.example.wenhai.listenall.moudle.playhistory.PlayHistoryFragment
import com.example.wenhai.listenall.utils.DAOUtil
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp


class LocalFragment : android.support.v4.app.Fragment() {
    @BindView(R.id.main_song_list)
    lateinit var mCollects: RecyclerView
    @BindView(R.id.main_local_scroll)
    lateinit var mScrollView: ScrollView
    @BindView(R.id.main_local_btn_liked_collect)
    lateinit var mBtnLikedCollect: Button
    @BindView(R.id.main_local_btn_my_collect)
    lateinit var mBtnMyCollect: Button
    @BindView(R.id.main_local_btn_liked)
    lateinit var mBtnLiked: ImageButton
    @BindView(R.id.main_local_btn_songs)
    lateinit var mBtnSongs: ImageButton
    @BindView(R.id.main_local_btn_recent_play)
    lateinit var mBtnRecentPlay: ImageButton

    private lateinit var mUnBinder: Unbinder
    private lateinit var mCollectAdapter: CollectAdapter

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
        mCollectAdapter = CollectAdapter(ArrayList())
        mCollects.adapter = mCollectAdapter
        mCollects.layoutManager = LinearLayoutManager(context)
    }


    override fun onResume() {
        super.onResume()
        showCollects(MY_COLLECT)
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
                showCollects(MY_COLLECT)
            }
            R.id.main_local_btn_liked_collect -> {
                showCollects(LIKED_COLLECT)
            }
        }

    }

    private fun showCollects(select: Int) {
        setButtonTextColor(select)
        if (select == MY_COLLECT) {
            val myCollects = ArrayList<Collect>()
            mCollectAdapter.setData(myCollects)
        } else {
            val dao = DAOUtil.getSession(context).likedCollectDao
            val likedCollectList = dao.queryBuilder().build().list()
            val collectList = ArrayList<Collect>()
            likedCollectList.mapTo(collectList) { it.collect }
            mCollectAdapter.setData(collectList)
        }
    }


    @Suppress("DEPRECATION")
    private fun setButtonTextColor(select: Int) {
        if (select == MY_COLLECT) {
            mBtnMyCollect.setTextColor(context.resources.getColor(R.color.colorBlack))
            mBtnLikedCollect.setTextColor(context.resources.getColor(R.color.colorGray))
        } else {
            mBtnMyCollect.setTextColor(context.resources.getColor(R.color.colorGray))
            mBtnLikedCollect.setTextColor(context.resources.getColor(R.color.colorBlack))
        }
    }

    fun showCollectDetail(collect: Collect) {
        val data = Bundle()
        val detailFragment = DetailFragment()
        data.putLong(DetailContract.ARGS_ID, collect.id)
        data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.COLLECT)
        detailFragment.arguments = data
        FragmentUtil.addFragmentToMainView(fragmentManager, detailFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    companion object {
        val TAG = "LocalFragment"
        val MY_COLLECT = 1
        val LIKED_COLLECT = 2
    }

    inner class CollectAdapter(var collects: List<Collect>) : RecyclerView.Adapter<CollectAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_liked_collect, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val collect = collects[position]
            holder !!.name.text = collect.title
            val displaySongNumber = "${collect.songCount}首"
            holder.songNumber.text = displaySongNumber
            GlideApp.with(context)
                    .load(collect.coverUrl)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(holder.cover)
            holder.itemView.setOnClickListener {
                showCollectDetail(collect)
            }

        }

        fun setData(data: List<Collect>) {
            collects = data
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = collects.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cover: ImageView = itemView.findViewById(R.id.liked_collect_cover)
            val name: TextView = itemView.findViewById(R.id.liked_collect_name)
            val songNumber: TextView = itemView.findViewById(R.id.liked_collect_song_number)
        }
    }
}



