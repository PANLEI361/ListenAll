package com.example.wenhai.listenall.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.JoinCollectsWithSongs
import com.example.wenhai.listenall.data.bean.JoinCollectsWithSongsDao
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.data.bean.SongDao
import com.example.wenhai.listenall.extension.hide
import com.example.wenhai.listenall.extension.showToast
import com.example.wenhai.listenall.module.main.local.EditCollectActivity
import com.example.wenhai.listenall.utils.DAOUtil
import com.example.wenhai.listenall.utils.GlideApp

class SelectCollectDialog(context: Context) : BaseBottomDialog(context) {

    @BindView(R.id.collects)
    lateinit var collects: RecyclerView

    var song: Song? = null

    override fun getLayoutResId(): Int {
        return R.layout.dialog_select_collect
    }

    override fun initView() {
        collects.layoutManager = LinearLayoutManager(context)
        val myCollects = DAOUtil.getSession(context).collectDao.queryBuilder().build().list()
        collects.adapter = CollectAdapter(myCollects)
    }

    @OnClick(R.id.create_collect)
    fun createCollect() {
        //新建歌单并把歌曲添加到新歌单中
        val intent = Intent(context, EditCollectActivity::class.java)
        intent.action = EditCollectActivity.ACTION_CREATE
        val data = Bundle()
        data.putSerializable("song", song)
        intent.putExtras(data)
        context.startActivity(intent)
        dismiss()
    }


    fun setChosenSong(song: Song): SelectCollectDialog {
        this.song = song
        return this
    }

    inner class CollectAdapter(var collects: List<Collect>) :
            RecyclerView.Adapter<CollectAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_liked_collect, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val collect = collects[position]
            holder!!.name.text = collect.title
            holder.songNumber.hide()
//            val displaySongNumber = "${collect.songCount}首"
//            holder.songNumber.text = displaySongNumber
            GlideApp.with(context)
                    .load(collect.coverUrl)
                    .error(R.drawable.ic_main_all_music)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(holder.cover)
            holder.itemView.setOnClickListener {
                saveNewRelationRecord(collect)
                dismiss()
            }

        }

        private fun saveNewRelationRecord(collect: Collect) {
            //添加歌曲到数据库，并获取歌曲在数据库中的id
            val songId = saveSongToDB(song)
            //添加关系到数据库
            val collectId = collect.id
            val dao = DAOUtil.getSession(context).joinCollectsWithSongsDao
            val resultList = dao.queryBuilder().where(JoinCollectsWithSongsDao.Properties.SongId.eq(songId),
                    JoinCollectsWithSongsDao.Properties.CollectId.eq(collectId))
                    .build().list()
            if (resultList.isEmpty()) {
                val relation = JoinCollectsWithSongs.newRecord(songId, collectId)
                if (dao.insert(relation) > 0) {
                    context.showToast("添加成功")
                    //更新歌单修改时间
                    collect.updateDate = System.currentTimeMillis() / 1000
                    DAOUtil.getSession(context).collectDao.update(collect)
                    //刷新数据，防止缓存导致的数据不更新
                    collect.resetSongs()
                } else {
                    context.showToast("添加失败")
                }
            } else {
                context.showToast("歌曲已存在")
            }
        }

        private fun saveSongToDB(song: Song?): Long {
            val songDao = DAOUtil.getSession(context).songDao
            val list = songDao.queryBuilder().where(SongDao.Properties.SongId.eq(song?.songId)).list()
            return if (list.isNotEmpty()) {
                list[0].id
            } else {
                songDao.insert(song)
            }
        }

        override fun getItemCount(): Int = collects.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cover: ImageView = itemView.findViewById(R.id.liked_collect_cover)
            val name: TextView = itemView.findViewById(R.id.liked_collect_name)
            val songNumber: TextView = itemView.findViewById(R.id.liked_collect_song_number)
        }
    }

}