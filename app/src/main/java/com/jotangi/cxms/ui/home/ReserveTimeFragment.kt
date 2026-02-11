package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.jotangi.cxms.Api.book.PhysicianTimeperiod
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentReserveTimeBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.ui.mylittlemin.AddVideoReserveOrderRequest
import com.jotangi.cxms.utils.DateTimeUtil
import com.jotangi.cxms.utils.DialogUtil
import kotlinx.coroutines.launch

class ReserveTimeFragment : BaseFragment() {

    private lateinit var binding: FragmentReserveTimeBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<ReserveTimeFragmentArgs>()
    private lateinit var timeListAdapter: TimeListAdapter
    private var timeList = ArrayList<PhysicianTimeperiod>()
    private lateinit var reserveDate: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReserveTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("可預約時段")

        timeListAdapter = TimeListAdapter(timeList)
        binding.apply {

            rvRtTime.layoutManager = GridLayoutManager(context, 2)
            rvRtTime.adapter = timeListAdapter
        }
    }

    private fun initHandler() {

        binding.apply {

            timeListAdapter.timeClick = {

                binding.tvRtPrice.text = "${timeList[it].price}"
                timeListAdapter.notifyDataSetChanged()
            }

            cvRtDate.setOnDateChangeListener { view, year, month, dayOfMonth ->

                refreshTimeList(
                    "${
                        year
                    }-${
                        DateTimeUtil.instance.fillZero(month + 1)
                    }-${
                        DateTimeUtil.instance.fillZero(dayOfMonth)
                    }"
                )
            }

            btRtNext.setOnClickListener {

                if (timeListAdapter.changeNum == -1 ||
                    binding.tvRtPrice.text.isEmpty()
                ) {

                    showErrorMsgDialog("您尚未選擇時段，請點選後繼續。")
                    return@setOnClickListener
                }

                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

                    lifecycleScope.launch {

                        bookViewModel.memberInfo {

                            lifecycleScope.launch {

                                bookViewModel.familyList {

                                    findNavController().navigate(
                                        ReserveTimeFragmentDirections
                                            .actionReserveTimeFragmentToWriteDataFragment(
                                                buildRequest()
                                            )
                                    )
                                }
                            }
                        }

                        dialog.dismiss()
                    }
                }
            }
        }
    }

    private fun buildRequest(): AddVideoReserveOrderRequest {
        val data = bookViewModel.physicianListLiveData.value!![args.position]
        val request = AddVideoReserveOrderRequest(
            data.store_id!!,
            data.did!!,
            data.pid!!,
            reserveDate,
            timeListAdapter.reserveTime,
            timeListAdapter.reserveEndtime,
            timeListAdapter.price
        )
        request.doctorName =
            bookViewModel.physicianListLiveData.value!![args.position].doctor_name!!
        return request
    }

    private fun initCallBack() {

        bookViewModel.physicianListLiveData.observe(viewLifecycleOwner) {

            val item = it[args.position]
            binding.tvRtDoctorName.text = "${item.doctor_name} ${item.job_title}"
        }

        bookViewModel.physicianWorkingDayListData.observe(viewLifecycleOwner) {

            refreshTimeList(DateTimeUtil.instance.getNowYMD())
        }
    }

    private fun refreshTimeList(nowYmd: String) {

        bookViewModel.physicianWorkingDayListData.value?.let {

            timeList.clear()
            reserveDate = nowYmd

            for (i in it.indices) {

                if (it[i].workingdate == nowYmd) {

                    it[i].timeperiod?.let { list ->
                        timeList.addAll(list)
                    }
                    break
                }
            }

            timeListAdapter.changeNum = -1
            binding.tvRtPrice.text = ""

            timeListAdapter.notifyDataSetChanged()
        }
    }
}