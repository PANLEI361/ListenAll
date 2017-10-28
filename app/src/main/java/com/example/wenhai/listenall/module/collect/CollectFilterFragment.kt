package com.example.wenhai.listenall.module.collect

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
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
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.extension.hide
import com.example.wenhai.listenall.extension.isShowing
import com.example.wenhai.listenall.extension.show
import com.example.wenhai.listenall.extension.showToast
import com.example.wenhai.listenall.module.detail.DetailContract
import com.example.wenhai.listenall.module.detail.DetailFragment
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.scwang.smartrefresh.layout.SmartRefreshLayout

class CollectFilterFragment : Fragment(), CollectFilterContract.View {

    @BindView(R.id.collect_filter)
    lateinit var mFilter: LinearLayout
    @BindView(R.id.collect_filter_title)
    lateinit var mFilterTitle: TextView
    @BindView(R.id.collect_filter_icon)
    lateinit var mFilterIcon: ImageView
    @BindView(R.id.collect_list)
    lateinit var mCollectList: RecyclerView
    @BindView(R.id.loading)
    lateinit var mLoading: LinearLayout
    @BindView(R.id.loading_failed)
    lateinit var mLoadFailed: LinearLayout
    @BindView(R.id.refresh)
    lateinit var mRefreshLayout: SmartRefreshLayout

    private lateinit var mUnbinder: Unbinder
    private lateinit var mCollectCategoryFragment: CollectCategoryFragment
    private var isFilterShown = false
    private var curCategory: String = ""
    private lateinit var mPresenter: CollectFilterContract.Presenter
    private var curPage = 1
    private lateinit var mCollectAdapter: CollectListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CollectFilterPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_collect, container, false)
        mUnbinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    override fun initView() {
        curCategory = mFilterTitle.text.toString()
        mPresenter.loadCollectByCategory(curCategory, curPage)
        mCollectList.layoutManager = LinearLayoutManager(context)
        mCollectAdapter = CollectListAdapter(context, ArrayList())
        mCollectList.adapter = mCollectAdapter
        mRefreshLayout.setOnLoadmoreListener {
            mPresenter.loadCollectByCategory(curCategory, curPage)
        }
    }

    fun setFilterTitle(category: String) {
        //所选分类与当前不同，加载新数据
        if (category != curCategory) {
            curCategory = category
            mCollectAdapter.clearData()
            curPage = 1
            mFilterTitle.text = curCategory
            //刷新数据
            mPresenter.loadCollectByCategory(curCategory, curPage)
        }
        setFilterTitleIcon(false)
    }

    fun setFilterTitleIcon(isFilterShow: Boolean) {
        isFilterShown = isFilterShow
        val iconId = if (isFilterShown) {
            R.drawable.ic_arrow_drop_up
        } else {
            R.drawable.ic_arrow_drop_down
        }
        mFilterIcon.setImageResource(iconId)
    }

    @OnClick(R.id.action_bar_back, R.id.collect_filter, R.id.collect_filter_action_bar,
            R.id.loading_failed)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                if (isFilterShown) {
                    mCollectCategoryFragment.onFilterChosen(curCategory)
                } else {
                    FragmentUtil.removeFragment(fragmentManager, this)
                }
            }
            R.id.collect_filter -> {
                if (! isFilterShown) {
                    mCollectCategoryFragment = CollectCategoryFragment()
                    val curCategory = mFilterTitle.text.toString()
                    val data = Bundle()
                    data.putString("curCategory", curCategory)
                    mCollectCategoryFragment.arguments = data
                    mCollectCategoryFragment.setTargetFragment(this, 0)
                    FragmentUtil.addFragmentToView(fragmentManager, mCollectCategoryFragment, R.id.collect_list_container)
                    setFilterTitleIcon(true)
                } else {
                    mCollectCategoryFragment.onFilterChosen(curCategory)
                }
            }
            R.id.collect_filter_action_bar -> {
                if (isFilterShown) {
                    mCollectCategoryFragment.onFilterChosen(curCategory)
                }
            }
            R.id.loading_failed -> {
                mPresenter.loadCollectByCategory(curCategory, curPage)
            }
        }
    }

    override fun setPresenter(presenter: CollectFilterContract.Presenter) {
        mPresenter = presenter
    }

    override fun getViewContext(): Context {
        return context
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

    override fun onLoading() {
        if (curPage == 1 || mLoadFailed.isShowing()) {
            mLoading.show()
            mCollectList.hide()
            mLoadFailed.hide()
        }
    }

    override fun onCollectLoad(collects: List<Collect>) {
        activity.runOnUiThread {
            if (mRefreshLayout.isLoading) {
                mRefreshLayout.finishLoadmore(200, true)
            }
            curPage ++
            mCollectAdapter.addData(collects)
            if (mLoading.isShowing()) {
                mLoading.hide()
                mCollectList.show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }

    inner class CollectListAdapter(val context: Context, var collects: List<Collect>) : RecyclerView.Adapter<CollectListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_collect_list, parent, false)
            return ViewHolder(itemView)
        }

        fun addData(addCollects: List<Collect>) {
            (collects as ArrayList<Collect>).addAll(addCollects)
            notifyDataSetChanged()
        }

        fun clearData() {
            (collects as ArrayList).clear()
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = collects.size


        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val collect = collects[position]
            if (holder != null) {
                holder.collectTitle.text = collect.title
                holder.collectDesc.text = collect.desc
                GlideApp.with(context)
                        .load(collect.coverUrl)
                        .placeholder(R.drawable.ic_main_collect)
                        .into(holder.collectCover)
                holder.item.setOnClickListener {
                    val detailFragment = DetailFragment()
                    val data = Bundle()
                    data.putLong(DetailContract.ARGS_ID, collect.collectId)
                    data.putSerializable(DetailContract.ARGS_LOAD_TYPE, DetailContract.LoadType.COLLECT)
                    detailFragment.arguments = data
                    FragmentUtil.addFragmentToMainView(fragmentManager, detailFragment)
                }
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var item: LinearLayout = itemView.findViewById(R.id.collect_list_ll)
            var collectTitle: TextView = itemView.findViewById(R.id.collect_list_title)
            var collectDesc: TextView = itemView.findViewById(R.id.collect_list_desc)
            var collectCover: ImageView = itemView.findViewById(R.id.collect_list_cover)
        }
    }

}