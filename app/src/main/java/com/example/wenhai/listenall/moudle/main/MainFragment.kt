package com.example.wenhai.listenall.moudle.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.moudle.main.MainContract.Presenter
import com.example.wenhai.listenall.moudle.main.local.LocalFragment
import com.example.wenhai.listenall.moudle.main.online.OnLineFragment
import com.example.wenhai.listenall.utils.LogUtil

/**
 * main Fragment
 *
 * Created by Wenhai on 2017/8/4.
 */


class MainFragment : Fragment(), MainContract.View {
    companion object {
        const val TAG = "MainFragment"

        const val PAGE_POSITION_MY_SONGS = 0
        const val PAGE_POSITION_ONLINE_SONGS = 1
        const val PAGE_COUNT = 2

    }

    @BindView(R.id.main_pager)
    lateinit var mPager: ViewPager
    @BindView(R.id.main_btn_local_songs)
    lateinit var mBtnMySongs: Button
    @BindView(R.id.main_btn_online_songs)
    lateinit var mBtnOnlineSongs: Button


    lateinit var mUnBinder: Unbinder
    lateinit var mPresenter: Presenter
    lateinit var mPagerAdapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        LogUtil.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        LogUtil.d(TAG, "onPause")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_main, container, false)
        mUnBinder = ButterKnife.bind(this, contentView)
        initView()
        LogUtil.d(TAG, "onCreateView")
        return contentView
    }

    override fun setPresenter(presenter: Presenter) {
        mPresenter = presenter
    }

    override fun initView() {
        mPagerAdapter = MainPagerAdapter(fragmentManager)
        mPager.adapter = mPagerAdapter
        onButtonClick(mBtnMySongs)
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
//                LogUtil.d(TAG, "scrollState=$state")
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                LogUtil.d(TAG, " scrolled position:$position,positionOffset:$positionOffset,positionOffsetPixels:$positionOffsetPixels")
            }

            @Suppress("DEPRECATION")
            override fun onPageSelected(position: Int) {
                when (position) {
                    PAGE_POSITION_MY_SONGS -> {
                        mBtnMySongs.setTextColor(resources.getColor(R.color.colorBlack))
                        mBtnOnlineSongs.setTextColor(resources.getColor(R.color.colorNotSelected))
                    }
                    PAGE_POSITION_ONLINE_SONGS -> {
                        mBtnOnlineSongs.setTextColor(resources.getColor(R.color.colorBlack))
                        mBtnMySongs.setTextColor(resources.getColor(R.color.colorNotSelected))
                    }
                }
            }

        })

    }

    @Suppress("DEPRECATION")
    @OnClick(R.id.main_btn_local_songs, R.id.main_btn_online_songs, R.id.main_btn_slide_menu)
    fun onButtonClick(v: View) {
        when (v.id) {
            R.id.main_btn_local_songs -> {
                mPager.currentItem = PAGE_POSITION_MY_SONGS
                mBtnMySongs.setTextColor(resources.getColor(R.color.colorBlack))
                mBtnOnlineSongs.setTextColor(resources.getColor(R.color.colorNotSelected))
            }
            R.id.main_btn_online_songs -> {
                mPager.currentItem = PAGE_POSITION_ONLINE_SONGS
                mBtnOnlineSongs.setTextColor(resources.getColor(R.color.colorBlack))
                mBtnMySongs.setTextColor(resources.getColor(R.color.colorNotSelected))
            }
            R.id.main_btn_slide_menu -> (activity as MainActivity).openDrawer()
        }
    }


    override fun onStop() {
        super.onStop()
        LogUtil.d(TAG, "onDestroyView")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtil.d(TAG, "onDestroyView")
        mUnBinder.unbind()
    }


    class MainPagerAdapter(fragmentManager: FragmentManager)
        : FragmentPagerAdapter(fragmentManager) {
        var onlineFragment: OnLineFragment? = null
        var localFragment: LocalFragment? = null

        init {
            LogUtil.d("PagerAdapter", "constructor")
        }

        override fun getItem(position: Int): Fragment {
            LogUtil.d("MainPagerAdapter", "getItem:$position")
            if (position == PAGE_POSITION_ONLINE_SONGS) {
                if (onlineFragment == null) {
                    onlineFragment = OnLineFragment()
                }
                return onlineFragment as OnLineFragment
            } else {
                if (localFragment == null) {
                    localFragment = LocalFragment()
                }
                return localFragment as LocalFragment
            }
        }

        override fun getCount(): Int = PAGE_COUNT
    }
}