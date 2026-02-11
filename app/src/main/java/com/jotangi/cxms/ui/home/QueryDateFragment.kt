package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentQueryDataBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogDateUtil
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import kotlinx.coroutines.launch

class QueryDateFragment : BaseFragment() {

    private lateinit var binding: FragmentQueryDataBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQueryDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
    }

    private fun initView() {

        setToolbarArrow("查詢日期")

        binding.apply {

            tvStart.text = WatchUtils.instance.currentYmdChina()
            tvEnd.text = WatchUtils.instance.currentYmdChina()
        }
    }

    private fun initHandler() {

        binding.apply {

            tvStart.setOnClickListener {
                DialogDateUtil.instance.twoYearDate(requireActivity(), tvStart)
            }

            tvEnd.setOnClickListener {
                DialogDateUtil.instance.twoYearDate(requireActivity(), tvEnd)
            }

            tvSend.setOnClickListener { send() }
        }
    }

    private fun send() {

        val start = WatchUtils.instance.changeTimeYmd(binding.tvStart.text.toString())
        val end = WatchUtils.instance.changeTimeYmd(binding.tvEnd.text.toString())

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                bookViewModel.hisMedicineList2(
                    start,
                    end,
                    success = {
                        findNavController().navigate(
                            R.id.drugDateFragment,
                            bundleOf(
                                "date" to "${binding.tvStart.text} - ${binding.tvEnd.text}"
                            )
                        )
                    },
                    fail = { showErrorMsgDialog(it) }
                )
                dialog.dismiss()
            }
        }
    }
}