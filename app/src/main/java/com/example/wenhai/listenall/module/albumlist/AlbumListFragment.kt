package com.example.wenhai.listenall.module.albumlist

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
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
import com.example.wenhai.listenall.extension.hide
import com.example.wenhai.listenall.extension.isShowing
import com.example.wenhai.listenall.extension.show
import com.example.wenhai.listenall.extension.showToast
import com.example.wenhai.listenall.module.detail.DetailContract
import com.example.wenhai.listenall.module.detail.DetailFragment
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.addFragmentToMainView
import com.example.wenhai.listenall.utils.removeFragment
import com.scwang.smartrefresh.layout.SmartRefreshLayout

class AlbumListFragment : Fragment(), AlbumListContract.View {
    @BindView(R.id.action_bar_title)
    lateinit var mTitle: TextView
    @BindView(R.id.new_albums)
    lateinit var mNewAlbumList: RecyclerView
    @BindView(R.id.loading)
    lateinit var mLoading: LinearLayout
    @BindView(R.id.refresh)
    lateinit var mRefreshLayout: SmartRefreshLayout
    @BindView(R.id.loading_failed)
    lateinit var mLoadFailed: LinearLayout

    lateinit var mPresenter: AlbumListContract.Presenter
    private lateinit var albumAdapter: AlbumListAdapter
    private lateinit var mUnBinder: Unbinder
    private var curPage = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AlbumListPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_album_list, container, false)
        mUnBinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    override fun initView() {
        mTitle.text = context.getString(R.string.main_new_songs)
        albumAdapter = AlbumListAdapter(context, ArrayList())
        mNewAlbumList.adapter = albumAdapter
        mNewAlbumList.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        mPresenter.loadNewAlbums(curPage)

        mRefreshLayout.setOnLoadmoreListener {
            mPresenter.loadNewAlbums(curPage)
        }

    }

    override fun onNewAlbumsLoad(albumList: List<Album>) {
        activity.runOnUiThread {
            curPage ++
            if (mRefreshLayout.isLoading) {
                mRefreshLayout.finishLoadmore(200, true)
            }
            albumAdapter.addData(albumList)
            if (mLoading.isShowing()) {
                mLoading.hide()
                mNewAlbumList.show()
            }
        }
    }


    override fun setPresenter(presenter: AlbumListContract.Presenter) {
        mPresenter = presenter
    }

    override fun getViewContext(): Context {
        return context
    }

    override fun onLoading() {
        if (curPage == 1 || mLoadFailed.isShowing()) {
            mLoading.show()
            mNewAlbumList.hide()
            mLoadFailed.hide()
        }
    }

    override fun onFailure(msg: String) {
        activity.runOnUiThread {
            if (mRefreshLayout.isLoading) {
                mRefreshLayout.finishLoadmore(200, false)
            }
            if (mLoading.isShowing()) {
                mLoading.hide()
                mLoadFailed.show()
            }
            context.showToast(msg)
        }
    }

    private fun showAlbumDetail(album: Album) {
        val data = Bundle()
        data.putLong(DetailContract.ARGS_ID, album.id)
        data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.ALBUM)
        val detailFragment = DetailFragment()
        detailFragment.arguments = data
        addFragmentToMainView(fragmentManager, detailFragment)
    }

    @OnClick(R.id.action_bar_back, R.id.loading_failed)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                removeFragment(fragmentManager, this)
            }
            R.id.loading_failed -> {
                mPresenter.loadNewAlbums(curPage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    inner class AlbumListAdapter(private val context: Context, private val albumList: List<Album>)
        : RecyclerView.Adapter<AlbumListAdapter.ViewHolder>() {
        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val album = albumList[position]
            holder !!.title.text = album.title
            holder.artist.text = album.artist
            GlideApp.with(context)
                    .load(album.coverUrl)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(holder.cover)
            holder.itemView.setOnClickListener {
                showAlbumDetail(album)
            }
        }

        override fun getItemCount(): Int = albumList.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_album_list, parent, false)
            return ViewHolder(itemView)
        }

        fun addData(data: List<Album>) {
            (albumList as ArrayList<Album>).addAll(data)
            notifyDataSetChanged()
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var cover: ImageView = itemView.findViewById(R.id.album_cover)
            var title: TextView = itemView.findViewById(R.id.album_title)
            var artist: TextView = itemView.findViewById(R.id.album_artist)
        }
    }

}