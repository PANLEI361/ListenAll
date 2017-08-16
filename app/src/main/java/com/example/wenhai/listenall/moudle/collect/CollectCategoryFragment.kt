package com.example.wenhai.listenall.moudle.collect

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.moudle.main.MainActivity
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.ScreenUtil


class CollectCategoryFragment : Fragment(), MainActivity.OnBackKeyEventListener {
    companion object {
        const val FILTER_LANGUAGE = 1
        const val FILTER_STYLE = 2
        const val FILTER_MOOD = 3
        const val FILTER_SCENE = 4
    }

    @BindView(R.id.filter_language)
    lateinit var mGridLanguage: GridLayout
    @BindView(R.id.filter_style)
    lateinit var mGridStyle: GridLayout
    @BindView(R.id.filter_mood)
    lateinit var mGridMood: GridLayout
    @BindView(R.id.filter_scene)
    lateinit var mGridScene: GridLayout
    @BindView(R.id.filter_scroll)
    lateinit var mFilterScroll: ScrollView

    @BindView(R.id.filter_all)
    lateinit var mFilterAll: TextView

    private val mFilterTextViews: ArrayList<TextView> = ArrayList()

    private lateinit var mUnbinder: Unbinder
    private lateinit var curCategory: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        curCategory = arguments.getString("curCategory")
        (activity as MainActivity).addBackKeyEventListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_collect_category, container, false)
        mUnbinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    private fun initView() {
        if (curCategory == "全部歌单") {
            mFilterAll.setBackgroundResource(R.drawable.bg_white_black_border)
        }
        val lan = context.resources.getStringArray(R.array.filter_language)
        val lanList = ArrayList<String>()
        lan.toCollection(lanList)
        setGridLayout(FILTER_LANGUAGE, lanList)
        val style = context.resources.getStringArray(R.array.filter_style)
        val styleList = ArrayList<String>()
        style.toCollection(styleList)
        setGridLayout(FILTER_STYLE, styleList)

        val mood = context.resources.getStringArray(R.array.filter_mood)
        val moodList = ArrayList<String>()
        mood.toCollection(moodList)
        setGridLayout(FILTER_MOOD, moodList)

        val scene = context.resources.getStringArray(R.array.filter_scene)
        val sceneList = ArrayList<String>()
        scene.toCollection(sceneList)
        setGridLayout(FILTER_SCENE, sceneList)
    }

    @OnClick(R.id.filter_all)
    fun onClick() {
        onFilterChosen(mFilterAll.text.toString())
    }

    private fun setGridLayout(filterType: Int, categories: ArrayList<String>) {

        //获得对应的 GridLayout
        val gridLayout = when (filterType) {
            FILTER_LANGUAGE -> {
                mGridLanguage
            }
            FILTER_MOOD -> {
                mGridMood
            }
            FILTER_SCENE -> {
                mGridScene
            }
            FILTER_STYLE -> {
                mGridStyle
            }
            else -> {
                mGridLanguage
            }
        }
        //获取对应的图标
        val headView = getGridHeadView(filterType)
        //设置参数
        val headLp = GridLayout.LayoutParams()
        headLp.rowSpec = GridLayout.spec(0, 2, GridLayout.FILL, 1f)
        headLp.columnSpec = GridLayout.spec(0, 1, GridLayout.FILL, 1f)
        headLp.bottomMargin = 1
        headLp.width = ScreenUtil.dp2px(context, 60f)
        headView.layoutParams = headLp

        gridLayout.addView(headView)

        val columnCount = gridLayout.columnCount
        //补齐空格
        if (categories.size > 6) {
            while ((categories.size - 6) % columnCount != 0) {
                categories.add("")
            }
        }
        for (i in 0 until categories.size) {
            val textView = getGridCellTextView(categories[i])
            if (! TextUtils.isEmpty(textView.text)) {
                textView.setOnClickListener {
                    textView.setBackgroundResource(R.drawable.bg_white_black_border)
                    onFilterChosen(textView.text.toString())
                }
            }
            if (textView.text == curCategory) {
                textView.setBackgroundResource(R.drawable.bg_white_black_border)
            }
            val lp = GridLayout.LayoutParams()
            lp.leftMargin = 1
            lp.bottomMargin = 1
            lp.height = ScreenUtil.dp2px(context, 35f)
            //宽度与 head 一样
            lp.width = ScreenUtil.dp2px(context, 60f)
            if (i < columnCount - 1) {
                lp.columnSpec = GridLayout.spec((i + 1) % columnCount, 1, GridLayout.FILL, 1f)
                lp.rowSpec = GridLayout.spec((i + 1) / columnCount, 1, GridLayout.FILL, 1f)
            } else {
                lp.columnSpec = GridLayout.spec((i + 2) % columnCount, 1, GridLayout.FILL, 1f)
                lp.rowSpec = GridLayout.spec((i + 2) / columnCount, 1, GridLayout.FILL, 1f)
            }
            textView.layoutParams = lp
            mFilterTextViews.add(textView)
            gridLayout.addView(textView)
        }
    }

    private fun onFilterChosen(filter: String) {
        curCategory = filter
        for (textView in mFilterTextViews) {
            @Suppress("DEPRECATION")
            textView.setBackgroundColor(context.resources.getColor(R.color.colorWhite))
        }
        (targetFragment as CollectFilterFragment).setFilterTitle(filter)
        FragmentUtil.removeFragment(fragmentManager, this)
    }

    @SuppressLint("InflateParams")
    private fun getGridHeadView(filterType: Int): LinearLayout {
        val viewGroup: ViewGroup = LayoutInflater.from(context).inflate(R.layout.head_view_for_filter, null) as ViewGroup
        val headView: LinearLayout = when (filterType) {
            FILTER_STYLE -> {
                viewGroup.findViewById(R.id.head_style)
            }
            FILTER_SCENE -> {
                viewGroup.findViewById(R.id.head_scene)
            }
            FILTER_MOOD -> {
                viewGroup.findViewById(R.id.head_mood)
            }
            FILTER_LANGUAGE -> {
                viewGroup.findViewById(R.id.head_language)
            }
            else -> {
                viewGroup.findViewById(R.id.head_language)
            }
        }
        viewGroup.removeView(headView)
        return headView
    }

    @SuppressLint("InflateParams")
    private fun getGridCellTextView(text: String): TextView {
        val viewGroup: ViewGroup = LayoutInflater.from(context).inflate(R.layout.view_for_filter, null) as ViewGroup
        val textView = viewGroup.findViewById<TextView>(R.id.grid_cell_text)
        textView.text = text
        viewGroup.removeView(textView)
        return textView
    }

    override fun onBackKeyPressed() {
        FragmentUtil.removeFragment(fragmentManager, this)
        (targetFragment as CollectFilterFragment).setFilterTitleIcon(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).removeBackKeyEventListener(this)
        mUnbinder.unbind()
    }

}