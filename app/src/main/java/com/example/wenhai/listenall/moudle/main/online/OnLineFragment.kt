package com.example.wenhai.listenall.moudle.main.online

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.moudle.albumlist.AlbumListFragment
import com.example.wenhai.listenall.moudle.artist.list.ArtistListFragment
import com.example.wenhai.listenall.moudle.collect.CollectFilterFragment
import com.example.wenhai.listenall.moudle.collectlist.CollectListFragment
import com.example.wenhai.listenall.moudle.detail.DetailFragment
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.ToastUtil
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.loader.ImageLoader

class OnLineFragment : android.support.v4.app.Fragment(), OnLineContract.View {
    override fun onFailure(msg: String) {
        ToastUtil.showToast(context, msg)
    }

    companion object {
        const val TAG = "OnLineFragment"
    }

    @BindView(R.id.main_banner)
    lateinit var mBanner: Banner
    @BindView(R.id.main_hot_collects)
    lateinit var mHotCollects: GridView
    @BindView(R.id.main_new_albums)
    lateinit var mNewAlbums: GridView
    @BindView(R.id.main_online_scroll)
    lateinit var mScrollView: ScrollView
    var mScrollY = 0

    lateinit var mUnBinder: Unbinder
    lateinit var mPresenter: OnLineContract.Presenter
    var isFirstStart = true

    lateinit var mHotCollectList: List<Collect>
    lateinit var mNewAlbumList: List<Album>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = OnLinePresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater !!.inflate(R.layout.fragment_main_online, container, false)
        mUnBinder = ButterKnife.bind(this, rootView)
        initView()
        return rootView
    }

    override fun setPresenter(presenter: OnLineContract.Presenter) {
        mPresenter = presenter
    }

    override fun onResume() {
        super.onResume()
        mBanner.startAutoPlay()
        if (isFirstStart) {
            isFirstStart = false
            mScrollView.smoothScrollTo(0, 0)
        } else {
            mScrollView.scrollTo(0, mScrollY)
        }
    }

    override fun onPause() {
        super.onPause()
        mScrollY = mScrollView.scrollY
        mBanner.stopAutoPlay()
    }

    @OnClick(R.id.main_btn_more_collect, R.id.main_btn_more_albums, R.id.main_online_btn_singer,
            R.id.main_online_btn_collect, R.id.main_online_btn_ranking_list)
    fun onClick(view: View) {
        when (view.id) {
            R.id.main_btn_more_collect -> {
                FragmentUtil.addFragmentToMainView(fragmentManager, CollectListFragment())
            }
            R.id.main_btn_more_albums -> {
                FragmentUtil.addFragmentToMainView(fragmentManager, AlbumListFragment())
            }
            R.id.main_online_btn_singer -> {
                FragmentUtil.addFragmentToMainView(fragmentManager, ArtistListFragment())
            }
            R.id.main_online_btn_collect -> {
                FragmentUtil.addFragmentToMainView(fragmentManager, CollectFilterFragment())
            }
        }
    }

    override fun initView() {
        initBanner()
        initHotCollectGirdView()
        initNewAlbumsGridView()
    }

    private fun initHotCollectGirdView() {
        mPresenter.loadHotCollects()
        mHotCollects.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val collect = mHotCollectList[position]
            val data = Bundle()
            data.putLong(DetailFragment.ARGS_ID, collect.id)
            data.putInt(DetailFragment.ARGS_TYPE, DetailFragment.TYPE_COLLECT)
            startDetailFragment(data)
        }
    }

    fun startDetailFragment(data: Bundle) {
        val detailFragment = DetailFragment()
        detailFragment.arguments = data
        FragmentUtil.addFragmentToMainView(fragmentManager, detailFragment)
    }

    override fun setHotCollects(hotCollects: List<Collect>) {
        mHotCollectList = hotCollects
        mHotCollects.adapter = HotCollectsAdapter(context, mHotCollectList)
    }

    private fun initNewAlbumsGridView() {
        mPresenter.loadNewAlbums()
        mNewAlbums.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val album = mNewAlbumList[position]
            val data = Bundle()
            data.putLong(DetailFragment.ARGS_ID, album.id)
            data.putInt(DetailFragment.ARGS_TYPE, DetailFragment.TYPE_ALBUM)
            startDetailFragment(data)
        }

    }

    override fun setNewAlbums(newAlbums: List<Album>) {
        mNewAlbumList = newAlbums
        mNewAlbums.adapter = NewAlbumsAdapter(context, mNewAlbumList)
    }

    private fun initBanner() {
        //设置指示器类型：圆形
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
        //设置指示器位置：水平居中
        mBanner.setIndicatorGravity(BannerConfig.CENTER)
        //设置图片加载器
        mBanner.setImageLoader(GlideLoaderForBanner())
        mBanner.setBannerAnimation(Transformer.Accordion)
        mPresenter.loadBanner(MusicProvider.XIAMI)
    }

    override fun setBanner(imgUrlList: List<String>) {
        mBanner.setImages(imgUrlList)
        mBanner.start()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }


    class GlideLoaderForBanner : ImageLoader() {
        override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
            GlideApp.with(context).load(path).into(imageView)
        }
    }

    class HotCollectsAdapter(val context: Context, var hotCollects: List<Collect>) : BaseAdapter() {
        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_main_hot_collect, parent, false)
            val cover = itemView.findViewById<ImageView>(R.id.main_iv_hot_collect_cover)
            val title = itemView.findViewById<TextView>(R.id.main_tv_hot_collect_title)
            val collect = hotCollects[position]
            GlideApp.with(context).load(collect.coverUrl).placeholder(R.drawable.ic_main_all_music)
                    .into(cover)
            title.text = collect.title
            return itemView
        }

        override fun getItem(position: Int): Any = hotCollects[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = hotCollects.size

    }

    class NewAlbumsAdapter(val context: Context, var newAlbums: List<Album>) : BaseAdapter() {
        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_main_new_album, parent, false)
            val cover = itemView.findViewById<ImageView>(R.id.main_iv_new_album_cover)
            val title = itemView.findViewById<TextView>(R.id.main_tv_new_album_title)
            val artist = itemView.findViewById<TextView>(R.id.main_tv_new_album_artist)
            val album = newAlbums[position]
            GlideApp.with(context).load(album.coverUrl).placeholder(R.drawable.ic_main_all_music)
                    .into(cover)
            title.text = album.title
            artist.text = album.artist
            return itemView
        }


        override fun getItem(position: Int): Any = newAlbums[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = newAlbums.size

    }
}