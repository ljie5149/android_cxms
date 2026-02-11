package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentReserveRegisteredBinding
import com.jotangi.cxms.databinding.ToolbarBinding

class ReserveRegisteredFragment : BaseFragment() {

    private lateinit var binding: FragmentReserveRegisteredBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReserveRegisteredBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("預約掛號")

        binding.apply {

            viewPager2.adapter = ReserveRegisteredAdapter(
                this@ReserveRegisteredFragment
            )

            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->

                tab.text = try {
                    listOf("依科別", "依醫師")[position]
                } catch (e: Exception) {
                    ""
                }
            }.attach()
        }
    }

    private fun initHandler() {


    }

    private fun initCallBack() {


    }
}