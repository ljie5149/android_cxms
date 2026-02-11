package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentReserveBinding
import com.jotangi.cxms.databinding.ToolbarBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ReserveFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReserveFragment2 : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentReserveBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar
//    companion object{
//        var did:Int = 1
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReserveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val btn_reserve: TextView = view.findViewById(R.id.btn_reserve)
        val adapter = ReserveViewPagerAdapter(this)
        viewPager.adapter = adapter
        setupToolBarReserve()
        btn_reserve.setOnClickListener {
            findNavController().navigate(R.id.action_reserveFragment2_to_calendarFragment

            )
        }

        viewPager.setCurrentItem(1, false)
        ReserveFragment.did = 2
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = when (position) {
//                0 -> "EECP"
//                1 -> "ILIB"
//                else -> null
//            }
//        }.attach()

//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                // `position` gives the currently selected tab index
//                when (position) {
//                    0 -> {
//                       did = 1
//                    }
//                    1 -> {
//                       did = 2
//                    }
//                }
//            }
//        })

    }


}