package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentDrugDateBinding
import com.jotangi.cxms.databinding.ToolbarBinding

class DrugDateFragment : BaseFragment() {

    private lateinit var binding: FragmentDrugDateBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var drugDateAdapter: DrugDateAdapter
    private var list = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrugDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("藥單日期")

        binding.apply {

            tvDate.text = arguments?.getString("date", "") ?: ""

            drugDateAdapter = DrugDateAdapter(list)

            binding.rv.apply {

                layoutManager = LinearLayoutManager(requireContext())
                adapter = drugDateAdapter
            }
        }
    }

    private fun initHandler() {

        drugDateAdapter.itemClick = {

            findNavController().navigate(
                R.id.drugDetailsFragment,
                bundleOf("date" to list[it])
            )
        }
    }

    private fun initCallBack() {

        bookViewModel.DrugInfoBeanLD.observe(viewLifecycleOwner) { beanList ->

            if (beanList.isNullOrEmpty()) return@observe

            val sList = beanList.mapNotNull { it.看診日 }.distinct()

            list.clear()
            list.addAll(sList)
            drugDateAdapter.notifyDataSetChanged()
        }
    }
}