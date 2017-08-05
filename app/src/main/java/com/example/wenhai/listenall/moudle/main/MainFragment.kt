package com.example.wenhai.listenall.moudle.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.moudle.main.MainContract.Presenter
import com.example.wenhai.listenall.moudle.main.local.LocalFragment
import com.example.wenhai.listenall.moudle.main.online.OnLineFragment
import com.example.wenhai.listenall.moudle.main.online.OnLinePresenter
import com.example.wenhai.listenall.utils.AppUtil
import com.example.wenhai.listenall.utils.LogUtil

/**
 * main Fragment
 *
 * Created by Wenhai on 2017/8/4.
 */


class MainFragment : Fragment(), MainContract.View {
    //views of slide menu
    @BindView(R.id.slide_menu_app_version)
    lateinit var smTvAppVersion: TextView
    @BindView(R.id.slide_only_wifi_switcher)
    lateinit var smSwitchOnlyWifi: Switch
    @BindView(R.id.slide_set_time_close_switcher)
    lateinit var smSwitchSetCloseTime: Switch
    @BindView(R.id.slide_menu_clean_cache)
    lateinit var smLlCleanCache: LinearLayout
    @BindView(R.id.slide_menu_tv_used_cache)
    lateinit var smTvUsedCache: TextView
    @BindView(R.id.slide_menu_set_cache_size)
    lateinit var smLlSetCacheSize: LinearLayout
    @BindView(R.id.slide_menu_tv_allowed_cache)
    lateinit var smTvAllowedCache: TextView
    @BindView(R.id.slide_menu_open_source)
    lateinit var smLlOpenSource: LinearLayout
    @BindView(R.id.slide_menu_about_app)
    lateinit var smLlAboutApp: LinearLayout
    @BindView(R.id.slide_menu_quit)
    lateinit var smBtnQuit: Button

    //views in main
    @BindView(R.id.main_pager)
    lateinit var mPager: ViewPager
    @BindView(R.id.main_btn_local_songs)
    lateinit var mBtnMySongs: Button
    @BindView(R.id.main_btn_online_songs)
    lateinit var mBtnOnlineSongs: Button
    @BindView(R.id.main_drawer)
    lateinit var mDrawer: DrawerLayout


    lateinit var mUnBinder: Unbinder
    lateinit var mPresenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        initSlideMenu()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_main, container, false)
        mUnBinder = ButterKnife.bind(this, contentView)
        initView()
        return contentView
    }

    override fun setPresenter(presenter: Presenter) {
        mPresenter = presenter
    }

    @Suppress("DEPRECATION")
    override fun initView() {
        mPager.adapter = MainPagerAdapter(fragmentManager)
        onButtonClick(mBtnMySongs)
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
//        mDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {
//            override fun onDrawerStateChanged(newState: Int) {
//            }
//
//            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
//            }
//
//            override fun onDrawerClosed(drawerView: View?) {
//            }
//
//            override fun onDrawerOpened(drawerView: View?) {
//            }
//
//        })

    }

    private fun initSlideMenu() {
        // TODO: 2017/8/4  初始化侧滑菜单
        val appVersion = AppUtil.getAppVersionName(context)
        val displayAppVersion = "V $appVersion"
        smTvAppVersion.text = displayAppVersion

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
            R.id.main_btn_slide_menu -> mDrawer.openDrawer(Gravity.START)
        }
    }

    @OnClick(R.id.slide_only_wifi_switcher, R.id.slide_set_time_close_switcher, R.id.slide_menu_clean_cache,
            R.id.slide_menu_set_cache_size, R.id.slide_menu_open_source, R.id.slide_menu_about_app, R.id.slide_menu_quit)
    fun onSlideMenuItemClick(v: View) {

        when (v.id) {
            R.id.slide_menu_quit -> {
                activity.finish()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
    }

    companion object {
        const val TAG = "MainActivity"

        const val PAGE_POSITION_MY_SONGS = 0
        const val PAGE_POSITION_ONLINE_SONGS = 1
        const val PAGE_COUNT = 2

    }

    class MainPagerAdapter(fragmentManager: FragmentManager)
        : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            if (position == PAGE_POSITION_ONLINE_SONGS) {
                val onLineFragment = OnLineFragment()
                OnLinePresenter(onLineFragment)
                return onLineFragment
            } else {
                return LocalFragment()
            }
        }

        override fun getCount(): Int = PAGE_COUNT
    }
}