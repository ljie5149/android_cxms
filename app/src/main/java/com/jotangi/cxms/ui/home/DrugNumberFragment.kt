package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.book.DrugInfoBean
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentDrugNumberBinding
import com.jotangi.cxms.databinding.ToolbarBinding

class DrugNumberFragment : BaseFragment() {

    private lateinit var binding: FragmentDrugNumberBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var drugNumberAdapter: DrugNumberAdapter
    private var list = ArrayList<DrugInfoBean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrugNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("領藥號碼")

        drugNumberAdapter = DrugNumberAdapter(list)

        binding.rv.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter = drugNumberAdapter
        }
    }

    private fun initCallBack() {

        bookViewModel.DrugInfoBeanLD.observe(viewLifecycleOwner) {

            if (it.isNullOrEmpty()) return@observe

            list.clear()
            list.addAll(it)
            drugNumberAdapter.notifyDataSetChanged()
        }
    }
}