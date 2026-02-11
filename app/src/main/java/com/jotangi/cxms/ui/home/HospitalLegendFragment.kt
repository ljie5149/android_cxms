package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentHospitalLegendBinding
import com.jotangi.cxms.databinding.ToolbarBinding

class HospitalLegendFragment : BaseFragment() {

    private lateinit var binding: FragmentHospitalLegendBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalLegendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("就醫說明")
    }

    private fun initHandler() {


    }

    private fun initCallBack() {


    }
}