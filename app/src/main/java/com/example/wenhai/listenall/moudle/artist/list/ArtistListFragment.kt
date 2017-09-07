package com.example.wenhai.listenall.moudle.artist.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.ArtistRegion
import com.example.wenhai.listenall.data.bean.Artist
import com.example.wenhai.listenall.extension.hide
import com.example.wenhai.listenall.extension.isShowing
import com.example.wenhai.listenall.extension.show
import com.example.wenhai.listenall.extension.showToast
import com.example.wenhai.listenall.moudle.artist.detail.ArtistDetailFragment
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.scwang.smartrefresh.layout.SmartRefreshLayout

class ArtistListFragment : Fragment(), ArtistListContract.View {

    @BindView(R.id.action_bar_title)
    lateinit var mTitle: TextView
    @BindView(R.id.artist_list)
    lateinit var mArtistList: RecyclerView
    @BindView(R.id.loading)
    lateinit var mLoading: LinearLayout
    @BindView(R.id.loading_failed)
    lateinit var mLoadFailed: LinearLayout
    @BindView(R.id.refresh)
    lateinit var mRefreshLayout: SmartRefreshLayout

    private lateinit var mTabs: ArrayList<Button>

    private lateinit var mPresenter: ArtistListContract.Presenter
    private lateinit var mUnbinder: Unbinder
    private lateinit var mArtistAdapter: ArtistAdapter
    private var curPage = 1
    private var curRegion: ArtistRegion = ArtistRegion.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArtistListPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_artist, container, false)
        mUnbinder = ButterKnife.bind(this, contentView)
        mTabs = ArrayList(5)
        mTabs.add(contentView.findViewById(R.id.singer_all))
        mTabs.add(contentView.findViewById(R.id.singer_china))
        mTabs.add(contentView.findViewById(R.id.singer_en))
        mTabs.add(contentView.findViewById(R.id.singer_japan))
        mTabs.add(contentView.findViewById(R.id.singer_korea))
        initView()
        return contentView
    }

    override fun setPresenter(presenter: ArtistListContract.Presenter) {
        mPresenter = presenter
    }


    override fun initView() {
        mTitle.text = getString(R.string.main_artist_list)
        mPresenter.loadArtists(curRegion, curPage)
        setTab(curRegion.ordinal)
        mArtistAdapter = ArtistAdapter(ArrayList())
        mArtistList.adapter = mArtistAdapter
        mArtistList.layoutManager = LinearLayoutManager(context)
        mRefreshLayout.setOnLoadmoreListener {
            mPresenter.loadArtists(curRegion, curPage)
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

    override fun onArtistsLoad(artists: List<Artist>) {
        activity.runOnUiThread {
            curPage ++
            if (mRefreshLayout.isLoading) {
                mRefreshLayout.finishLoadmore(200, true)
            }
            mArtistAdapter.addData(artists)
            if (mLoading.isShowing()) {
                mLoading.hide()
                mArtistList.show()
            }
        }
    }

    override fun onLoading() {
        if (curPage == 1 || mLoadFailed.isShowing()) {
            mLoading.show()
            mArtistList.hide()
            mLoadFailed.hide()
        }
    }

    @OnClick(R.id.action_bar_back, R.id.loading_failed)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                FragmentUtil.removeFragment(fragmentManager, this)
            }
            R.id.loading_failed -> {
                mPresenter.loadArtists(curRegion, curPage)
            }
        }
    }

    @OnClick(R.id.singer_all, R.id.singer_china, R.id.singer_en, R.id.singer_japan, R.id.singer_korea)
    fun onTabClick(view: View) {
        val newRegion = when (view.id) {
            R.id.singer_all -> {
                ArtistRegion.ALL
            }
            R.id.singer_china -> {
                ArtistRegion.CN
            }
            R.id.singer_en -> {
                ArtistRegion.EA
            }
            R.id.singer_japan -> {
                ArtistRegion.JP
            }
            R.id.singer_korea -> {
                ArtistRegion.KO
            }
            else -> {
                ArtistRegion.ALL
            }
        }
        if (curRegion != newRegion) {
            curRegion = newRegion
            curPage = 1
            mArtistAdapter.clear()
            mPresenter.loadArtists(curRegion, curPage)
            setTab(curRegion.ordinal)
        }
    }

    @Suppress("DEPRECATION")
    private fun setTab(position: Int) {
        for (tab in mTabs) {
            tab.setTextColor(context.resources.getColor(R.color.colorGray))
        }
        mTabs[position].setTextColor(context.resources.getColor(R.color.colorBlack))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }

    inner class ArtistAdapter(private val artists: List<Artist>) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {
        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val artist = artists[position]
            holder !!.artistName.text = artist.name
            GlideApp.with(context)
                    .load(artist.miniImgUrl)
                    .placeholder(R.drawable.ic_main_singer)
                    .into(holder.artistImg)
            holder.itemView.setOnClickListener {
                val artistDetailFragment = ArtistDetailFragment()
                val data = Bundle()
                data.putParcelable("artist", artist)
                artistDetailFragment.arguments = data
                FragmentUtil.addFragmentToMainView(fragmentManager, artistDetailFragment)
            }
        }

        fun addData(data: List<Artist>) {
            (artists as ArrayList).addAll(data)
            notifyDataSetChanged()
        }

        fun clear() {
            (artists as ArrayList).clear()
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_artist_list, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount(): Int = artists.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val artistImg: ImageView = itemView.findViewById(R.id.item_artist_img)
            val artistName: TextView = itemView.findViewById(R.id.item_artist_name)
        }
    }
}