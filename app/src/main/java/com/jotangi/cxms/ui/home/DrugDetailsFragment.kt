package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.book.DrugItemList
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentDrugDetailsBinding
import com.jotangi.cxms.databinding.ToolbarBinding

class DrugDetailsFragment : BaseFragment() {

    private lateinit var binding: FragmentDrugDetailsBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var drugDetailsAdapter: DrugDetailsAdapter
    private var list = ArrayList<DrugItemList>()
    private lateinit var date: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrugDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("藥單明細")

        drugDetailsAdapter = DrugDetailsAdapter(list)

        date = arguments?.getString("date", "") ?: ""

        binding.apply {

            tvDate.text = date

            rv.layoutManager = LinearLayoutManager(requireContext())
            rv.adapter = drugDetailsAdapter
        }
    }

    private fun initCallBack() {

        bookViewModel.DrugInfoBeanLD.observe(viewLifecycleOwner) { beanList ->

            if (beanList.isNullOrEmpty()) return@observe

            val sameDate = beanList.filter { it.看診日 == date }
            val itemList = mutableListOf<DrugItemList>()
            sameDate.forEach {
                itemList.addAll(it.用藥項目清單 ?: mutableListOf())
            }

            list.clear()
            list.addAll(itemList)
            drugDetailsAdapter.notifyDataSetChanged()
        }
    }
}