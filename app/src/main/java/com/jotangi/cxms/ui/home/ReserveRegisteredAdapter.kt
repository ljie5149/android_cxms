package com.jotangi.cxms.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ReserveRegisteredAdapter(fragment: ReserveRegisteredFragment) :
    FragmentStateAdapter(fragment) {

    private val list = arrayListOf(
        DepartmentFragment.newInstance(),
        DoctorFragment.newInstance()
    )

    override fun getItemCount() = list.size

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}