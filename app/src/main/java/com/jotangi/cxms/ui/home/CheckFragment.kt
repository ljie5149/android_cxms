package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentCheckBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtil
import kotlinx.coroutines.launch

class CheckFragment : BaseFragment() {

    private lateinit var binding: FragmentCheckBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
    }

    private fun initView() {

        setToolbarArrow("檢查/檢驗")
    }

    private fun initHandler() {

        binding.apply {

            tvItem.setOnClickListener {

                DialogUtil.instance.loadingShow(requireActivity()) {dialog ->

                    lifecycleScope.launch {

                        bookViewModel.hisCheckItemList2(
                            success = { findNavController().navigate(R.id.waitCheckFragment) },
                            fail = { showErrorMsgDialog(it) }
                        )

                        dialog.dismiss()
                    }
                }
            }

            tvNumber.setOnClickListener {
                findNavController().navigate(R.id.numberFragment)
            }

            tvProgress.setOnClickListener {
                findNavController().navigate(R.id.progressQueryFragment)
            }
        }
    }
}