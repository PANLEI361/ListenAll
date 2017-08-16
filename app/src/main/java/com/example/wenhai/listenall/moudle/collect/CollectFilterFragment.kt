package com.example.wenhai.listenall.moudle.collect

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
import com.example.wenhai.listenall.moudle.detail.DetailFragment
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.ToastUtil

class CollectFilterFragment : Fragment(), CollectFilterContract.View {

    @BindView(R.id.collect_filter)
    lateinit var mFilter: LinearLayout
    @BindView(R.id.collect_filter_title)
    lateinit var mFilterTitle: TextView
    @BindView(R.id.collect_filter_icon)
    lateinit var mFilterIcon: ImageView
    @BindView(R.id.collect_list)
    lateinit var mCollectList: RecyclerView

    private lateinit var mUnbinder: Unbinder
    private lateinit var mCollectCategoryFragment: CollectCategoryFragment
    private var isFilterShown = false
    private var curCategory: String = ""
    private lateinit var mPresenter: CollectFilterContract.Presenter

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

        mPresenter.loadCollectByCategory(curCategory)
    }

    fun setFilterTitle(category: String) {
        //所选分类与当前不同，加载新数据
        if (category != curCategory) {
            curCategory = category
            mFilterTitle.text = curCategory
            setFilterTitleIcon(false)
            //刷新数据
            mPresenter.loadCollectByCategory(curCategory)
        }
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

    @OnClick(R.id.action_bar_back, R.id.collect_filter)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                if (isFilterShown) {
                    FragmentUtil.removeFragment(fragmentManager, mCollectCategoryFragment)
                    isFilterShown = false
                    mFilterIcon.setImageResource(R.drawable.ic_arrow_drop_down)
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
                }
            }
        }
    }

    override fun setPresenter(presenter: CollectFilterContract.Presenter) {
        mPresenter = presenter
    }

    override fun onFailure(msg: String) {
        ToastUtil.showToast(context, msg)
    }

    override fun onCollectLoad(collects: List<Collect>) {
        activity.runOnUiThread {
            mCollectList.layoutManager = LinearLayoutManager(context)
            mCollectList.adapter = CollectListAdapter(context, collects)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }

    inner class CollectListAdapter(val context: Context, private var collects: List<Collect>) : RecyclerView.Adapter<CollectListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(context).inflate(R.layout.item_collect_list, parent, false)
            return ViewHolder(itemView)
        }

//        fun setData(newCollects: List<Collect>) {
//            collects = newCollects
//            notifyDataSetChanged()
//        }
//
//        fun addData(addCollects: List<Collect>) {
//            (collects as ArrayList<Collect>).addAll(addCollects)
//            notifyDataSetChanged()
//        }

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
                    data.putLong(DetailFragment.ARGS_ID, collect.id)
                    data.putInt(DetailFragment.ARGS_TYPE, DetailFragment.TYPE_COLLECT)
                    val detailFragment = DetailFragment()
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