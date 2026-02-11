package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentSelectMonthBinding
import com.jotangi.cxms.databinding.ToolbarBinding

class SelectMonthFragment : BaseFragment() {

    private lateinit var binding: FragmentSelectMonthBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectMonthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("請選擇月份")
    }

    private fun initHandler() {


    }

    private fun initCallBack() {

        bookViewModel.BannerList2BeanLD.observe(viewLifecycleOwner) {

            binding.apply {

                try {

                    tvOne.text = it[0].bannerDescript.toString()
                    tvOne.setOnClickListener {
                        findNavController().navigate(
                            R.id.timeTableFragment,
                            bundleOf("index" to 0)
                        )
                    }
                } catch (e: Exception) {

                    tvOne.visibility = View.GONE
                    e.printStackTrace()
                }

                try {

                    tvTwo.text = it[1].bannerDescript.toString()
                    tvTwo.setOnClickListener {
                        findNavController().navigate(
                            R.id.timeTableFragment,
                            bundleOf("index" to 1)
                        )
                    }
                } catch (e: Exception) {

                    tvTwo.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        }
    }
}