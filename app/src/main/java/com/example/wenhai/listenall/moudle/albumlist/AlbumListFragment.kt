package com.example.wenhai.listenall.moudle.albumlist

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.moudle.detail.DetailFragment
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.ToastUtil

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
        mGridNewAlbums.onItemClickListener = AdapterView.OnItemClickListener {
            _, _, position, _ ->
            val album = mAlbumList[position]
            val data = Bundle()
            data.putLong(DetailFragment.ARGS_ID, album.id)
            data.putInt(DetailFragment.ARGS_TYPE, DetailFragment.TYPE_ALBUM)
            val detailFragment = DetailFragment()
            detailFragment.arguments = data
            FragmentUtil.addFragmentToMainView(fragmentManager, detailFragment)
        }
        mPresenter.loadNewAlbums()

    }

    override fun setNewAlbums(albumList: List<Album>) {
        mAlbumList = albumList
        mGridNewAlbums.adapter = AlbumListAdapter(context, mAlbumList)
    }

    override fun onFailure(msg: String) {
        ToastUtil.showToast(context, msg)
    }

    @OnClick(R.id.action_bar_back)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                FragmentUtil.removeFragment(fragmentManager, this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    internal class AlbumListAdapter(private val context: Context, private val albumList: List<Album>) : BaseAdapter() {

        override fun getCount(): Int = albumList.size

        override fun getItem(i: Int): Any = albumList[i]

        override fun getItemId(i: Int): Long = i.toLong()

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
            var itemView = convertView
            var viewHolder: ViewHolder
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.item_album_list, viewGroup, false)
                viewHolder = ViewHolder(itemView)
                itemView.tag = viewHolder
            }
            viewHolder = itemView !!.tag as ViewHolder
            val album = albumList[position]
            viewHolder.title.text = album.title
            viewHolder.artist.text = album.artist
            GlideApp.with(context)
                    .load(album.coverUrl)
                    .placeholder(R.drawable.ic_main_all_music)
                    .into(viewHolder.cover)

            return itemView
        }

        inner class ViewHolder(itemView: View) {
            var cover: ImageView = itemView.findViewById(R.id.album_cover)
            var title: TextView = itemView.findViewById(R.id.album_title)
            var artist: TextView = itemView.findViewById(R.id.album_artist)
        }
    }

}