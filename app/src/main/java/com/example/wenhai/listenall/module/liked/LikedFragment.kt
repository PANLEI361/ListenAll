package com.example.wenhai.listenall.module.liked

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.utils.removeFragment

class LikedFragment : Fragment() {
    @BindView(R.id.action_bar_title)
    lateinit var mTitle: TextView
    @BindView(R.id.liked_tab)
    lateinit var mTab: TabLayout
    @BindView(R.id.liked_pager)
    lateinit var mPager: ViewPager

    private lateinit var mUnbinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val itemView = inflater !!.inflate(R.layout.fragment_liked, container, false)
        mUnbinder = ButterKnife.bind(this, itemView)
        initView()
        return itemView
    }

    private fun initView() {
        mTitle.text = getString(R.string.main_like)
        mPager.adapter = PagerAdapter(childFragmentManager)
        mTab.setupWithViewPager(mPager)
    }

    @OnClick(R.id.action_bar_back)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                removeFragment(fragmentManager, this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }


    class PagerAdapter(fragmentManager: android.support.v4.app.FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> LikedSongFragment()
                1 -> LikedAlbumFragment()
                else -> LikedSongFragment()
            }
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "单曲"
                1 -> "专辑"
                else -> ""
            }
        }
    }
}