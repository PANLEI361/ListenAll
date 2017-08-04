package com.example.wenhai.listenall.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.utils.LogUtil

const val PAGE_COUNT = 2
const val PAGE_POSITION_MY_SONGS = 0
const val PAGE_POSITION_ONLINE_SONGS = 1

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    @BindView(R.id.main_pager)
    lateinit var mPager: ViewPager
    @BindView(R.id.main_btn_my_songs)
    lateinit var mBtnMySongs: Button
    @BindView(R.id.main_btn_online_songs)
    lateinit var mBtnOnlineSongs: Button
    @BindView(R.id.main_drawer)
    lateinit var mDrawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        initView()
    }

    @Suppress("DEPRECATION")
    private fun initView() {
        mPager.adapter = MainPagerAdapter(supportFragmentManager)
        click(mBtnMySongs)
//        mPager.currentItem = PAGE_POSITION_MY_SONGS
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                LogUtil.d(TAG, "scrollState=$state")
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                LogUtil.d(TAG, " scrolled position:$position,positionOffset:$positionOffset,positionOffsetPixels:$positionOffsetPixels")
            }

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
                LogUtil.d(TAG, "selected:$position")
            }

        })

    }


    @Suppress("DEPRECATION")
    @OnClick(R.id.main_btn_my_songs, R.id.main_btn_online_songs, R.id.main_btn_slide_menu)
    fun click(v: View) {
        when (v.id) {
            R.id.main_btn_my_songs -> {
                mPager.currentItem = PAGE_POSITION_MY_SONGS
                mBtnMySongs.setTextColor(resources.getColor(R.color.colorBlack))
                mBtnOnlineSongs.setTextColor(resources.getColor(R.color.colorNotSelected))
            }
            R.id.main_btn_online_songs -> {
                mPager.currentItem = PAGE_POSITION_ONLINE_SONGS
                mBtnOnlineSongs.setTextColor(resources.getColor(R.color.colorBlack))
                mBtnMySongs.setTextColor(resources.getColor(R.color.colorNotSelected))
            }
            R.id.main_btn_slide_menu -> mDrawer.openDrawer(Gravity.START)
        }
    }

    class MainPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            if (position == PAGE_POSITION_ONLINE_SONGS) {
                return OnLineSongsFragment()
            } else {
                return MySongsFragment()
            }
        }

        override fun getCount(): Int = PAGE_COUNT
    }

}
