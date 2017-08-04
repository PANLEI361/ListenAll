package com.example.wenhai.listenall.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bumptech.glide.Glide
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.SongCollect
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.loader.ImageLoader

/**
 * Created by Wenhai on 2017/7/30.
 */
class OnLineSongsFragment : android.support.v4.app.Fragment(), BaseView {

    @BindView(R.id.main_banner)
    lateinit var mBanner: Banner
    @BindView(R.id.main_hot_collects)
    lateinit var mHotCollects: GridView
    @BindView(R.id.main_new_albums)
    lateinit var mNewAlbums: GridView

    lateinit var mUnBinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater !!.inflate(R.layout.fragment_main_on_line, container, false)
        mUnBinder = ButterKnife.bind(this, rootView)
        initView()
        return rootView
    }

    override fun initView() {
        initBanner()
        initHotCollectGirdView()
        initNewAlbumsGridView()
    }

    private fun initHotCollectGirdView() {
        val fakeHotCollect = ArrayList<SongCollect>()

        for (i in 1..6) {
            val hotCollect = SongCollect()
            hotCollect.title = "this is a test"
            fakeHotCollect.add(hotCollect)
        }

        mHotCollects.adapter = HotCollectsAdapter(activity, fakeHotCollect)


    }

    private fun initNewAlbumsGridView() {

        val fakeHotCollect = ArrayList<SongCollect>()

        for (i in 1..6) {
            val hotCollect = SongCollect()
            hotCollect.title = "this is a test"
            fakeHotCollect.add(hotCollect)
        }

        mNewAlbums.adapter = HotCollectsAdapter(activity, fakeHotCollect)


    }

    private fun initBanner() {
        //设置指示器类型：圆形
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
        //设置指示器位置：水平居中
        mBanner.setIndicatorGravity(BannerConfig.CENTER)
        //设置图片加载器
        mBanner.setImageLoader(GlideLoaderForBanner())
        mBanner.setBannerAnimation(Transformer.Accordion)
        //设置图片
        val imgUrl = ArrayList<String>()
        imgUrl.add("http://p1.music.126.net/qTeah-W84aTYmVv8D3WVDw==/18607035278817847.jpg")
        imgUrl.add("http://p1.music.126.net/lFq5b9d_yyLPJOlHOalquA==/18730180581161155.jpg")
        imgUrl.add("http://p1.music.126.net/5iligU8K49--QicUzXaXHw==/18524571906724282.jpg")
        imgUrl.add("http://p1.music.126.net/WlVrWAz5n4SKAukEAXG2oA==/18607035278817840.jpg")
        imgUrl.add("http://p1.music.126.net/lyyBe_RcUf9HYauXaAz4ng==/18607035278817845.jpg")
        imgUrl.add("http://p1.music.126.net/VLhQ9k42lUA24UmpkEWapA==/18607035278817836.jpg")
        imgUrl.add("http://p1.music.126.net/14FSmx_WIlscmEsHt14SLQ==/18730180581161152.jpg")
        imgUrl.add("http://p1.music.126.net/Q3zTFc_jcafp834HkKPvMQ==/19208468137507367.jpg")
        mBanner.setImages(imgUrl)
        mBanner.start()
    }

    override fun onStart() {
        super.onStart()
        mBanner.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        mBanner.stopAutoPlay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }
}

class GlideLoaderForBanner : ImageLoader() {
    override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
        Glide.with(context).load(path).into(imageView)
    }
}

class HotCollectsAdapter(val context: Context, var hotCollects: ArrayList<SongCollect>) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_main_hot_collect, parent, false)

        val cover = itemView.findViewById<ImageView>(R.id.main_iv_hot_collect_cover)
        cover.setImageResource(R.drawable.ic_main_all_music)
        val title: TextView = itemView.findViewById(R.id.main_tv_hot_collect_title)
        title.text = hotCollects[position].title

        return itemView
    }


    override fun getItem(position: Int): Any = hotCollects[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = hotCollects.size

}