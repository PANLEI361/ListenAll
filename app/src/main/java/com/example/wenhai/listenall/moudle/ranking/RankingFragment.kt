package com.example.wenhai.listenall.moudle.ranking

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.moudle.detail.DetailContract
import com.example.wenhai.listenall.moudle.detail.DetailFragment
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp

class RankingFragment : Fragment(), RankingContract.View {
    @BindView(R.id.action_bar_title)
    lateinit var mTitle: TextView
    @BindView(R.id.ranking_official)
    lateinit var mOfficialRanking: RecyclerView
    @BindView(R.id.ranking_global)
    lateinit var mGlobalRanking: GridView
    @BindView(R.id.loading)
    lateinit var mLoading: LinearLayout
    @BindView(R.id.loading_failed)
    lateinit var mLoadFailed: LinearLayout
    @BindView(R.id.content)
    lateinit var mContent: LinearLayout

    lateinit var mPresenter: RankingContract.Presenter
    private lateinit var mUnbinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RankingPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView: View = inflater !!.inflate(R.layout.fragment_ranking, container, false)
        mUnbinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    override fun initView() {
        mPresenter.loadOfficialRanking(MusicProvider.XIAMI)
        mTitle.text = context.getString(R.string.main_ranking_list)
        initGlobalRankingView()
    }

    private fun initGlobalRankingView() {
        val titles = context.resources.getStringArray(R.array.global_ranking).toList()
        val covers = intArrayOf(R.drawable.ranking_billboard, R.drawable.ranking_uk, R.drawable.ranking_oricon)
        val data = ArrayList<Map<String, Any>>()
        (0 until titles.size).mapTo(data) { hashMapOf<String, Any>(Pair("title", titles[it]), Pair("cover", covers[it])) }
        mGlobalRanking.adapter = SimpleAdapter(context, data,
                R.layout.item_ranking_global,
                arrayOf("title", "cover"),
                intArrayOf(R.id.ranking_global_title, R.id.ranking_global_cover))

        mGlobalRanking.setOnItemClickListener { _, _, i, _ ->
            val ranking = when (i) {
                0 -> RankingContract.GlobalRanking.BILLBOARD
                1 -> RankingContract.GlobalRanking.UK
                2 -> RankingContract.GlobalRanking.ORICON
                else -> {
                    RankingContract.GlobalRanking.BILLBOARD
                }
            }
            val detailFragment = DetailFragment()
            val args = Bundle()
            args.putSerializable(DetailContract.ARGS_GLOBAL_RANKING, ranking)
            args.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.GLOBAL_RANKING)
            detailFragment.arguments = args
            FragmentUtil.addFragmentToMainView(fragmentManager, detailFragment)
        }
    }

    @OnClick(R.id.action_bar_back, R.id.loading_failed)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                FragmentUtil.removeFragment(fragmentManager, this)
            }
            R.id.loading_failed -> {
                mPresenter.loadOfficialRanking(MusicProvider.XIAMI)
            }
        }
    }


    override fun setPresenter(presenter: RankingContract.Presenter) {
        mPresenter = presenter
    }


    override fun onOfficialRankingLoad(collects: List<Collect>) {
        activity.runOnUiThread {
            mOfficialRanking.adapter = OfficialRankingAdapter(collects)
            mOfficialRanking.layoutManager = LinearLayoutManager(context)
            mLoading.visibility = View.GONE
            mContent.visibility = View.VISIBLE
        }
    }

    fun showRankingDetail(collect: Collect) {
        val detailFragment = DetailFragment()
        val args = Bundle()
        args.putParcelable(DetailContract.ARGS_COLLECT, collect)
        args.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.OFFICIAL_RANKING)
        detailFragment.arguments = args
        FragmentUtil.addFragmentToMainView(fragmentManager, detailFragment)
    }

    override fun onLoading() {
        mLoading.visibility = View.VISIBLE
        mContent.visibility = View.GONE
        mLoadFailed.visibility = View.GONE
    }

    override fun onFailure(msg: String) {
        activity.runOnUiThread {
            mLoading.visibility = View.GONE
            mContent.visibility = View.GONE
            mLoadFailed.visibility = View.VISIBLE
//            ToastUtil.showToast(context, msg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }

    inner class OfficialRankingAdapter(private val rankingCollects: List<Collect>) : RecyclerView.Adapter<OfficialRankingAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val collect = rankingCollects[position]
            GlideApp.with(context)
                    .load(collect.coverDrawable)
                    .into(holder?.rankingCover)

            val firstSong = collect.songs[0]
            val firstPreview = "1.${firstSong.name}-${firstSong.artistName}"
            holder?.songPreview0?.text = firstPreview

            val secondSong = collect.songs[1]
            val secondPreview = "2.${secondSong.name}-${secondSong.artistName}"
            holder?.songPreview1?.text = secondPreview

            val thirdSong = collect.songs[2]
            val thirdPreview = "3.${thirdSong.name}-${thirdSong.artistName}"
            holder?.songPreview2?.text = thirdPreview

            holder?.itemView?.setOnClickListener {
                showRankingDetail(collect)
            }
        }

        override fun getItemCount(): Int = rankingCollects.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = layoutInflater.inflate(R.layout.item_ranking_list, parent, false)
            return ViewHolder(itemView)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val rankingCover: ImageView = itemView.findViewById(R.id.ranking_cover)
            val songPreview0: TextView = itemView.findViewById(R.id.ranking_preview0)
            val songPreview1: TextView = itemView.findViewById(R.id.ranking_preview1)
            val songPreview2: TextView = itemView.findViewById(R.id.ranking_preview2)
        }
    }
}