package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentReserveVideoConsultBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.ui.mylittlemin.GetWorkingDay4Request
import com.jotangi.cxms.utils.DateTimeUtil
import com.jotangi.cxms.utils.DialogUtil
import kotlinx.coroutines.launch

class ReserveVideoConsultFragment : BaseFragment() {

    private lateinit var binding: FragmentReserveVideoConsultBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var doctorListAdapter: DoctorListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReserveVideoConsultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {
        setToolbarArrow("預約視訊諮詢")
    }

    private fun initCallBack() {

        bookViewModel.physicianListLiveData.observe(viewLifecycleOwner) { list ->

            doctorListAdapter = DoctorListAdapter(list)
            binding.rvRvc.apply {

                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = doctorListAdapter
            }

            doctorListAdapter.doctorClick = { position ->

                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

                    lifecycleScope.launch {

                        val item = list[position]
                        bookViewModel.getWorkingDay4(
                            GetWorkingDay4Request(
                                item.store_id!!,
                                item.pid!!,
                                DateTimeUtil.instance.getNowYMD()
                            )
                        ) {
                            findNavController().navigate(
                                ReserveVideoConsultFragmentDirections
                                    .actionReserveVideoConsultFragmentToReserveTimeFragment(
                                        position
                                    )
                            )
                        }

                        dialog.dismiss()
                    }
                }
            }
        }
    }
}