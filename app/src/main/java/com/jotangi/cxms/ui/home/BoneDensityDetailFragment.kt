package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentBoneDensityDetailBinding
import com.jotangi.cxms.databinding.ToolbarBinding

class BoneDensityDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentBoneDensityDetailBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var boneDensityDetailAdapter: BoneDensityDetailAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoneDensityDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {

        setToolbarArrow("骨質密度紀錄")

        watchViewModel.getBmdList.observe(viewLifecycleOwner) { beanList ->

            boneDensityDetailAdapter = BoneDensityDetailAdapter(beanList)

            binding.rvBdi.apply {

                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = boneDensityDetailAdapter
            }
        }
    }
}