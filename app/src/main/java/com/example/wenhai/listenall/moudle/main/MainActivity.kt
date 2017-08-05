package com.example.wenhai.listenall.moudle.main

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.utils.ActivityUtil
import com.example.wenhai.listenall.utils.AppUtil


class MainActivity : AppCompatActivity() {
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

    @BindView(R.id.main_drawer)
    lateinit var mDrawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        var mainFragment: MainFragment? = supportFragmentManager.findFragmentById(R.id.main_container) as? MainFragment
        if (mainFragment == null) {
            mainFragment = MainFragment()
            ActivityUtil.addFragmentToActivity(supportFragmentManager, mainFragment, R.id.main_container)
        }
        initSlideMenu()
    }


    @OnClick(R.id.slide_only_wifi_switcher, R.id.slide_set_time_close_switcher, R.id.slide_menu_clean_cache,
            R.id.slide_menu_set_cache_size, R.id.slide_menu_open_source, R.id.slide_menu_about_app, R.id.slide_menu_quit)
    fun onSlideMenuItemClick(v: View) {
        when (v.id) {
            R.id.slide_menu_quit -> {
                finish()
            }
        }
    }

    private fun initSlideMenu() {
        // TODO: 2017/8/4  初始化侧滑菜单
        val appVersion = AppUtil.getAppVersionName(this)
        val displayAppVersion = "V $appVersion"
        smTvAppVersion.text = displayAppVersion

    }

    fun openDrawer() {
        mDrawer.openDrawer(Gravity.START)
    }

}
