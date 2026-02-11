package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentDrugInfoBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtil
import kotlinx.coroutines.launch

class DrugInfoFragment : BaseFragment() {

    private lateinit var binding: FragmentDrugInfoBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrugInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
    }

    private fun initView() {

        setToolbarArrow("領藥資訊")
    }

    private fun initHandler() {

        binding.apply {

            tvMyDrug.setOnClickListener {
                findNavController().navigate(R.id.queryDateFragment)
            }
            tvDrugInfo.setOnClickListener { myDrugApi() }
        }
    }

    private fun myDrugApi() {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                bookViewModel.hisMedicineInfo(
                    success = { findNavController().navigate(R.id.drugNumberFragment) },
                    fail = { showErrorMsgDialog(it) }
                )

                dialog.dismiss()
            }
        }
    }
}