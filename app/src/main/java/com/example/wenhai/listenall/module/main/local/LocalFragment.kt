package com.example.wenhai.listenall.module.main.local

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.LikedCollectDao
import com.example.wenhai.listenall.extension.hide
import com.example.wenhai.listenall.extension.show
import com.example.wenhai.listenall.extension.showToast
import com.example.wenhai.listenall.module.detail.DetailContract
import com.example.wenhai.listenall.module.detail.DetailFragment
import com.example.wenhai.listenall.module.liked.LikedFragment
import com.example.wenhai.listenall.module.playhistory.PlayHistoryFragment
import com.example.wenhai.listenall.utils.DAOUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.addFragmentToMainView


class LocalFragment : android.support.v4.app.Fragment() {
    @BindView(R.id.main_song_list)
    lateinit var mCollects: RecyclerView
    //    @BindView(R.id.main_local_scroll)
//    lateinit var mScrollView: ScrollView
    @BindView(R.id.main_local_btn_liked_collect)
    lateinit var mBtnLikedCollect: Button
    @BindView(R.id.main_local_btn_my_collect)
    lateinit var mBtnMyCollect: Button
    @BindView(R.id.main_local_create_collect)
    lateinit var mBtnCreateCollect: ImageButton

    private lateinit var mUnBinder: Unbinder
    private lateinit var mCollectAdapter: CollectAdapter
    private var curShowType = MY_COLLECT//当前显示的歌单类型（收藏歌单或者自建歌单）

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater!!.inflate(R.layout.fragment_main_local, container, false)
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
        showCollects()
    }

    @OnClick(R.id.main_local_btn_songs, R.id.main_local_btn_recent_play, R.id.main_local_btn_liked,
            R.id.main_local_btn_my_collect, R.id.main_local_btn_liked_collect, R.id.main_local_create_collect)
    fun onClick(v: View) {
        when (v.id) {
            R.id.main_local_btn_songs -> {//本地歌曲
            }

            R.id.main_local_btn_recent_play -> {//最近播放
                addFragmentToMainView(fragmentManager, PlayHistoryFragment())
            }

            R.id.main_local_btn_liked -> {//收藏
                addFragmentToMainView(fragmentManager, LikedFragment())
            }

            R.id.main_local_btn_my_collect -> {//我的歌单
                curShowType = MY_COLLECT
                showCollects()
            }

            R.id.main_local_btn_liked_collect -> {//收藏歌单
                curShowType = LIKED_COLLECT
                showCollects()
            }

            R.id.main_local_create_collect -> {//创建歌单
                val intent = Intent(context, EditCollectActivity::class.java)
                intent.action = EditCollectActivity.ACTION_CREATE
                startActivityForResult(intent, REQUEST_CREATE_COLLECT)
            }
        }

    }

    /**
     * 显示歌单
     */
    fun showCollects() {
        setButtonTextColor(curShowType)
        if (curShowType == MY_COLLECT) {//显示自建歌单
            mBtnCreateCollect.show()
            val collectDao = DAOUtil.getSession(context).collectDao
            val myCollects = collectDao.queryBuilder().build().list()
            mCollectAdapter.setData(myCollects)
        } else {//显示收藏歌单
            mBtnCreateCollect.hide()
            val dao = DAOUtil.getSession(context).likedCollectDao
            val likedCollectList = dao.queryBuilder()
                    .orderDesc(LikedCollectDao.Properties.LikedTime)
                    .build().list()
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

    private fun showCollectDetail(collect: Collect) {
        val data = Bundle()
        val detailFragment = DetailFragment()
        if (curShowType == MY_COLLECT) {
            data.putLong(DetailContract.ARGS_ID, collect.id)
            data.putBoolean(DetailContract.ARGS_IS_USER_COLLECT, true)
            //用于删除歌单时刷新显示
            detailFragment.localFragment = this
        } else {
            data.putLong(DetailContract.ARGS_ID, collect.collectId)
            data.putBoolean(DetailContract.ARGS_IS_USER_COLLECT, false)
        }
        data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.COLLECT)
        detailFragment.arguments = data
        addFragmentToMainView(fragmentManager, detailFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CREATE_COLLECT && resultCode == RESULT_COLLECT_CREATED) {
            context.showToast("歌单创建成功")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    companion object {
        val TAG = "LocalFragment"
        val MY_COLLECT = 1
        val LIKED_COLLECT = 2
        val REQUEST_CREATE_COLLECT = 0x00
        val RESULT_COLLECT_CREATED = 0x01
    }

    inner class CollectAdapter(var collects: List<Collect>)
        : RecyclerView.Adapter<CollectAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_liked_collect, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val collect = collects[position]
            holder!!.name.text = collect.title
            val songCount = if (collect.isFromUser) {
                collect.songs.size
            } else {
                collect.songCount
            }
            val displaySongNumber = "$songCount 首"
            holder.songNumber.text = displaySongNumber
            GlideApp.with(context)
                    .load(collect.coverUrl)
                    .error(R.drawable.ic_main_all_music)
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



