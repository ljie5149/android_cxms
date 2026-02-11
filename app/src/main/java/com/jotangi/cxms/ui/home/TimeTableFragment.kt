package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentTimeTableBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import java.lang.Exception

class TimeTableFragment : BaseFragment() {

    private lateinit var binding: FragmentTimeTableBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {


    }

    private fun initHandler() {


    }

    private fun initCallBack() {

        bookViewModel.BannerList2BeanLD.observe(viewLifecycleOwner) {

            if (it.isEmpty()) return@observe

            val index = arguments?.getInt("index", -1) ?: -1

            if (index == -1) return@observe

            setToolbarArrow(it[index].bannerDescript.toString())

            binding.wv.apply {

                with(settings) {

                    builtInZoomControls = true
                    loadWithOverviewMode = true
                    useWideViewPort = true

                    loadDataWithBaseURL(
                        ApiConstant.IMAGE_URL,
                        "<img src='${it[index].bannerPicture}'/>",
                        "text/html",
                        "utf-8",
                        null
                    )
                }
            }

            try {

//                Glide.with(requireContext())
//                    .load("${ApiConstant.IMAGE_URL}${it[0].bannerPicture}")
//                    .into(binding.iv01)
//
//                Glide.with(requireContext())
//                    .load("${ApiConstant.IMAGE_URL}${it[1].bannerPicture}")
//                    .into(binding.iv02)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}