package com.example.wenhai.listenall.moudle.collectlist

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
import com.example.wenhai.listenall.moudle.detail.DetailContract
import com.example.wenhai.listenall.moudle.detail.DetailFragment
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.ToastUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout

internal class CollectListFragment : Fragment(), CollectListContract.View {

    companion object {
        const val TAG = "CollectListFragment"
    }

    @BindView(R.id.collect_list)
    lateinit var mRvCollectList: RecyclerView
    @BindView(R.id.action_bar_title)
    lateinit var mTitle: TextView
    @BindView(R.id.loading)
    lateinit var mLoading: LinearLayout
    @BindView(R.id.refresh)
    lateinit var mRefreshLayout: SmartRefreshLayout

    private lateinit var mUnBinder: Unbinder
    private lateinit var mPresenter: CollectListContract.Presenter
    private lateinit var mCollectListAdapter: CollectListAdapter
    private var curLoadPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CollectListPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_collect_list, container, false)
        mUnBinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    @OnClick(R.id.action_bar_back)
    fun onActionClick() {
        FragmentUtil.removeFragment(fragmentManager, this)
    }

    override fun initView() {
        mTitle.text = context.getString(R.string.main_hot_collect)
        mRvCollectList.layoutManager = LinearLayoutManager(context)
        mCollectListAdapter = CollectListAdapter(context, ArrayList())
        mRvCollectList.adapter = mCollectListAdapter
        mPresenter.loadCollects(curLoadPage)

        mRefreshLayout.isEnableRefresh = false
        mRefreshLayout.isEnableAutoLoadmore = false
        mRefreshLayout.setOnLoadmoreListener {
            mPresenter.loadCollects(curLoadPage)
        }
    }

    override fun setCollects(collects: List<Collect>) {
        activity.runOnUiThread {
            //page++ when current page load success
            curLoadPage ++
            if (mRefreshLayout.isLoading) {
                mRefreshLayout.finishLoadmore(200, true)
            }
            mCollectListAdapter.addData(collects)
            mLoading.visibility = View.GONE
            mRvCollectList.visibility = View.VISIBLE
        }
    }

    override fun onLoading() {
        if (curLoadPage == 1) {
            mLoading.visibility = View.VISIBLE
            mRvCollectList.visibility = View.GONE
        }
    }

    override fun onFailure(msg: String) {
        activity.runOnUiThread {
            if (mRefreshLayout.isLoading) {
                mRefreshLayout.finishLoadmore(200, false)
            }
            ToastUtil.showToast(context, msg)
        }
    }


    override fun setPresenter(presenter: CollectListContract.Presenter) {
        mPresenter = presenter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    inner class CollectListAdapter(val context: Context, private var collects: List<Collect>) : RecyclerView.Adapter<CollectListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_collect_list, parent, false)
            return ViewHolder(itemView)
        }

        fun addData(addCollects: List<Collect>) {
            (collects as ArrayList<Collect>).addAll(addCollects)
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
                    val data = Bundle()
                    val detailFragment = DetailFragment()
                    data.putLong(DetailContract.ARGS_ID, collect.id)
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