package com.example.wenhai.listenall.module.liked

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.LikedAlbum
import com.example.wenhai.listenall.data.bean.LikedAlbumDao
import com.example.wenhai.listenall.module.detail.DetailContract
import com.example.wenhai.listenall.module.detail.DetailFragment
import com.example.wenhai.listenall.utils.DAOUtil
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp

class LikedAlbumFragment : Fragment() {

    @BindView(R.id.liked_album)
    lateinit var mAlbumList: RecyclerView
    lateinit private var mUnbinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_liked_album, container, false)
        mUnbinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    private fun initView() {
        val dao = DAOUtil.getSession(context).likedAlbumDao
        val list: List<LikedAlbum> = dao.queryBuilder()
                .orderDesc(LikedAlbumDao.Properties.LikedTime)
                .build()
                .list()
        mAlbumList.adapter = LikedAlbumListAdapter(list)
        mAlbumList.layoutManager = LinearLayoutManager(context)
    }

    private fun showAlbumDetail(album: Album) {
        val data = Bundle()
        data.putLong(DetailContract.ARGS_ID, album.id)
        data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.ALBUM)
        val detailFragment = DetailFragment()
        detailFragment.arguments = data
        FragmentUtil.addFragmentToMainView(parentFragment.fragmentManager, detailFragment)
    }


    inner class LikedAlbumListAdapter(private val albumList: List<LikedAlbum>) : RecyclerView.Adapter<LikedAlbumListAdapter.ViewHolder>() {
        override fun getItemCount(): Int = albumList.size

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val album = albumList[position].album
            holder !!.albumName.text = album.title
            holder.artistName.text = album.artist
            GlideApp.with(context)
                    .load(album.coverUrl)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(holder.albumCover)
            holder.itemView.setOnClickListener {
                showAlbumDetail(album)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_liked_album, parent, false)
            return ViewHolder(itemView)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val albumName: TextView = itemView.findViewById(R.id.liked_album_name)
            val artistName: TextView = itemView.findViewById(R.id.liked_album_artist)
            val albumCover: ImageView = itemView.findViewById(R.id.liked_album_cover)
        }
    }
}