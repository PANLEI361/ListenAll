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
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Banner
import com.example.wenhai.listenall.data.bean.BannerType
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.ktextension.hide
import com.example.wenhai.listenall.ktextension.isShowing
import com.example.wenhai.listenall.ktextension.show
import com.example.wenhai.listenall.ktextension.showToast
import com.example.wenhai.listenall.moudle.albumlist.AlbumListFragment
import com.example.wenhai.listenall.moudle.artist.list.ArtistListFragment
import com.example.wenhai.listenall.moudle.collect.CollectFilterFragment
import com.example.wenhai.listenall.moudle.collectlist.CollectListFragment
import com.example.wenhai.listenall.moudle.detail.DetailContract
import com.example.wenhai.listenall.moudle.detail.DetailFragment
import com.example.wenhai.listenall.moudle.ranking.RankingFragment
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.LogUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.loader.ImageLoader

class OnLineFragment : android.support.v4.app.Fragment(), OnLineContract.View {
    @BindView(R.id.main_banner)
    lateinit var mBanner: com.youth.banner.Banner
    @BindView(R.id.main_hot_collects)
    lateinit var mHotCollects: GridView
    @BindView(R.id.main_new_albums)
    lateinit var mNewAlbums: GridView
    @BindView(R.id.main_online_scroll)
    lateinit var mScrollView: ScrollView
    @BindView(R.id.refresh)
    lateinit var mRefreshLayout: SmartRefreshLayout
    @BindView(R.id.refresh_header)
    lateinit var mRefreshHeader: ClassicsHeader
    @BindView(R.id.loading)
    lateinit var mLoading: LinearLayout
    @BindView(R.id.loading_failed)
    lateinit var mFailed: LinearLayout
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout

    private var mScrollY = 0
    private lateinit var mUnBinder: Unbinder
    private lateinit var mPresenter: OnLineContract.Presenter
    private var isFirstStart = true

    private lateinit var mHotCollectAdapter: HotCollectsAdapter
    private lateinit var mNewAlbumAdapter: NewAlbumsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = OnLinePresenter(this)
    }

    override fun setPresenter(presenter: OnLineContract.Presenter) {
        mPresenter = presenter
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater !!.inflate(R.layout.fragment_main_online, container, false)
        mUnBinder = ButterKnife.bind(this, rootView)
        initView()
        return rootView
    }

    override fun initView() {
        initHotCollectGirdView()
        initNewAlbumsGridView()
        initBanner()
        initRefreshLayout()
        loadData()
    }

    private fun initHotCollectGirdView() {
        mHotCollects.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val collect = mHotCollectAdapter.hotCollects[position]
            val data = Bundle()
            data.putLong(DetailContract.ARGS_ID, collect.id)
            data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.COLLECT)
            showDetail(data)
        }
    }

    private fun showDetail(data: Bundle) {
        val detailFragment = DetailFragment()
        detailFragment.arguments = data
        FragmentUtil.addFragmentToMainView(fragmentManager, detailFragment)
    }

    private fun initNewAlbumsGridView() {
        mNewAlbums.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val album = mNewAlbumAdapter.newAlbums[position]
            val data = Bundle()
            data.putLong(DetailContract.ARGS_ID, album.id)
            data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.ALBUM)
            showDetail(data)
        }
    }

    private fun initBanner() {
        //设置指示器类型：圆形
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
        //设置指示器位置：水平居中
        mBanner.setIndicatorGravity(BannerConfig.CENTER)
        //设置图片加载器
        mBanner.setImageLoader(GlideLoaderForBanner())
        mBanner.setBannerAnimation(Transformer.Accordion)
    }


    private fun initRefreshLayout() {
        mRefreshLayout.setOnRefreshListener {
            mPresenter.loadBanner(MusicProvider.XIAMI)
            mPresenter.loadHotCollects()
            mPresenter.loadHotCollects()
        }
        mRefreshLayout.isEnableLoadmore = false
    }

    //加载 banner、热门歌单和最新专辑
    private fun loadData() {
        mPresenter.loadBanner(MusicProvider.XIAMI)
        mPresenter.loadHotCollects()
        mPresenter.loadNewAlbums()
    }


    @OnClick(R.id.main_btn_more_collect, R.id.main_btn_more_albums, R.id.main_online_btn_singer,
            R.id.main_online_btn_collect, R.id.main_online_btn_ranking_list, R.id.loading_failed)
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
            R.id.main_online_btn_ranking_list -> {
                FragmentUtil.addFragmentToMainView(fragmentManager, RankingFragment())
            }
            R.id.loading_failed -> {
                loadData()
            }
        }
    }

    override fun onLoading() {
        mLoading.show()
        mFailed.hide()
        mContent.hide()
    }

    override fun onFailure(msg: String) {
        activity.runOnUiThread {
            if (mRefreshLayout.isRefreshing) {
                mRefreshLayout.finishRefresh(200, false)
            }
            if (mLoading.isShowing()) {
                mLoading.hide()
                mFailed.show()
            }
            context.showToast(msg)
        }
    }

    override fun onHotCollectsLoad(hotCollects: List<Collect>) {
        activity.runOnUiThread {
            if (mRefreshLayout.isRefreshing) {
                mRefreshLayout.finishRefresh(200, true)
            }
            mHotCollectAdapter = HotCollectsAdapter(context, hotCollects.subList(0, 6))
            mHotCollects.adapter = mHotCollectAdapter
            if (mLoading.isShowing()) {
                mLoading.hide()
                mContent.show()
            }
        }
    }


    override fun onNewAlbumsLoad(newAlbums: List<Album>) {
        activity.runOnUiThread {
            if (mRefreshLayout.isRefreshing) {
                mRefreshLayout.finishRefresh(200, true)
            }
            mNewAlbumAdapter = NewAlbumsAdapter(context, newAlbums.subList(0, 6))
            mNewAlbums.adapter = mNewAlbumAdapter
            if (mLoading.isShowing()) {
                mLoading.hide()
                mContent.show()
            }
        }
    }


    override fun onBannerLoad(banners: List<Banner>) {
        activity.runOnUiThread {
            if (mRefreshLayout.isRefreshing) {
                mRefreshLayout.finishRefresh(200, true)
            }
            val imgUrls = ArrayList<String>()
            banners.mapTo(imgUrls) { it.imgUrl }
            mBanner.setImages(imgUrls)
            mBanner.start()
            mBanner.setOnBannerListener { position: Int ->
                onBannerClick(banners[position])
            }
        }
    }

    private fun onBannerClick(clickBanner: Banner) {
        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        when (clickBanner.type) {
            BannerType.SONG -> {
                val data = Bundle()
                data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.SONG)
                data.putLong(DetailContract.ARGS_ID, clickBanner.id)
                showDetail(data)
            }
            BannerType.ALBUM -> {
                val data = Bundle()
                data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.ALBUM)
                data.putLong(DetailContract.ARGS_ID, clickBanner.id)
                showDetail(data)
            }
            BannerType.COLLECT -> {
                LogUtil.d(TAG, "click collect")
            }
            BannerType.OTHER -> {
                LogUtil.d(TAG, "click other")
            }
        }

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


    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    companion object {
        const val TAG = "OnLineFragment"
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