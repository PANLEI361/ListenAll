package com.example.wenhai.listenall.moudle.albumlist

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.moudle.detail.DetailActivity
import com.example.wenhai.listenall.moudle.detail.Type

class AlbumListFragment : Fragment(), AlbumListContract.View {
    @BindView(R.id.action_bar_title)
    lateinit var mTitle: TextView
    @BindView(R.id.new_albums)
    lateinit var mGridNewAlbums: GridView

    lateinit var mAlbumList: List<Album>

    lateinit var mPresenter: AlbumListContract.Presenter
    lateinit var mUnBinder: Unbinder


    override fun setPresenter(presenter: AlbumListContract.Presenter) {
        mPresenter = presenter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_album_list, container, false)
        mUnBinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    override fun initView() {
        mTitle.text = context.getString(R.string.main_new_songs)
        mGridNewAlbums.onItemClickListener = AdapterView.OnItemClickListener {
            _, _, position, _ ->
            val album = mAlbumList[position]
            val id = album.id
            val type = Type.ALBUM.ordinal
            val data = Bundle()
            data.putLong("id", id)
            data.putInt("type", type)
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtras(data)
            startActivity(intent)
        }
        mPresenter.loadNewAlbums()

    }

    override fun setNewAlbums(albumList: List<Album>) {
        mAlbumList = albumList
        mGridNewAlbums.adapter = NewAlbumAdapter(context, mAlbumList)
    }

    @OnClick(R.id.action_bar_back)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                activity.finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

}