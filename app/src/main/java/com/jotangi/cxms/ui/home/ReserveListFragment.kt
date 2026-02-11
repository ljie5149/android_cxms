package com.jotangi.cxms.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.Api.book.apirequest.HisRegistrationRequest
import com.jotangi.cxms.Api.book.apiresponse.PhysicianScheduleData
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentReserveListBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.jackyVariant.Common
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReserveListFragment : BaseFragment() {

    private lateinit var binding: FragmentReserveListBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private var list = listOf<PhysicianScheduleData>()

    private lateinit var assignDayAdapter: AssignDayAdapter
    private var dayList = listOf<AssignDayList>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReserveListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
    }

    private fun initView() {

        assignDayAdapter = AssignDayAdapter()

        binding.rv.apply {

            adapter = assignDayAdapter

            layoutManager = LinearLayoutManager(requireContext())
        }

        bookViewModel.physicianScheduleLD.value?.let {
Log.d("micCheckK", it.toString())
            list = it
            if (list.isEmpty()) {
                setToolbarArrow("無資料")
                return@let
            }

            Log.w(TAG, "List: ${it}")

            var name = ""
            for (i in it) {
                if (i.科別 == "特別門診") {
                    name = i.科別.toString()
                } else {
                    name = i.科別.toString()
                    break
                }
            }

            setToolbarArrow(name)

            KiziCalendar.instance.init(it, binding.calendarView)
        }
    }

    private fun initHandler() {

        binding.apply {

            ivLeft.setOnClickListener {
                KiziCalendar.instance.setDownDate()
            }

            ivRight.setOnClickListener {
                KiziCalendar.instance.setUpDate()
            }
        }

        KiziCalendar.instance.setDateValue(dateListener)

        assignDayAdapter.callClick = {

            startActivity(
                Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:" + "03-333-0567")
                )
            )
        }

        assignDayAdapter.registerClick = {

            DialogUtil.instance.reserve(
                requireActivity(),
                WatchUtils.instance.chinaDayToYmdw(it.日期.toString()),
                it,
                okClick = { data ->
                    hisRegistration(data)
                }
            )
        }
    }

    private val dateListener: KiziCalendar.DateListener = object :
        KiziCalendar.DateListener {
        override fun calendarDate(date: String) {
            binding.tvDate.text = date
        }

        override fun clickDate(list: List<PhysicianScheduleData>) {
            Log.d("micCheckNB", "call")
            rvDay(list)
        }
    }

    private fun rvDay(list: List<PhysicianScheduleData>) {

        val morningList = list.filter { it.班別 == "上午診" }
        val noonList = list.filter { it.班別 == "下午診" }
        val nightList = list.filter { it.班別 == "晚診" }

        dayList = listOf()

        assignDayAdapter.submitList(dayList)

        dayList = dayList.toMutableList().apply {

            add(AssignDayList.Title("早診"))

            morningList.forEach { add(AssignDayList.Item(it)) }
        }

        dayList = dayList.toMutableList().apply {

            add(AssignDayList.Title("午診"))

            noonList.forEach { add(AssignDayList.Item(it)) }
        }

        dayList = dayList.toMutableList().apply {

            add(AssignDayList.Title("晚診"))

            nightList.forEach { add(AssignDayList.Item(it)) }
        }

        assignDayAdapter.submitList(dayList)
    }

    private fun hisRegistration(data: PhysicianScheduleData) {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                bookViewModel.hisRegistration2(
                    HisRegistrationRequest(
                        Common.getToken(),
                        WatchUtils.instance.ymdChinaToWestern(data.日期.toString()),
                        data.排班識別碼!!
                    ),
                    success = {

                        CoroutineScope(Dispatchers.Main).launch {

                            DialogUtil.instance.reserveHint(
                                requireActivity(),
                                WatchUtils.instance.ymdChinaToWestern(data.日期.toString()),
                                it,
                                okClick = { data1 ->

                                    SharedPreferencesUtil.instances.setOrder(
                                        "sid=${
                                            ApiUrl.c_sid
                                        }&member_pid=${
                                            SharedPreferencesUtil.instances.getAccountPid()
                                        }&division_name=${
                                            it.科別
                                        }&doctor_name=${
                                            it.醫師
                                        }"
                                    )
                                    DialogUtil.instance.reserveAgree(
                                        requireActivity(),
                                        WatchUtils.instance.ymdChinaToWestern(data.日期.toString()),
                                        it,
                                        okClick = { data2 ->
                                            findNavController().navigate(
                                                ReserveListFragmentDirections.actionReserveListFragmentToWebFragment(
                                                    getString(R.string.pre_dia_question)
                                                )
                                            )
                                        },
                                        cancelClick = {
                                            goHome()
                                        }
                                    )
                                },
                                cancelClick = {
                                    goHome()
                                }
                            )
                        }
                    },
                    fail = {

                        showErrorMsgDialog(it)
                    }
                )

                dialog.dismiss()
            }
        }
    }
}