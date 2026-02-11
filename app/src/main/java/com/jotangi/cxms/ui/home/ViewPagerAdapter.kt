package com.jotangi.cxms.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: HomeFragment) : FragmentStateAdapter(activity) {

    private val fragmentList = arrayListOf(
        AFragment.newInstance(), BFragment.newInstance()
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}