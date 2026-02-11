package com.jotangi.cxms.ui.home

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.Api.book.apiresponse.SleepWellWorkingDayBean
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentSleepWellBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.SleepWellAdapterData
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SleepWellFragment : BaseFragment() {

    private lateinit var binding: FragmentSleepWellBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var sleepWellAdapter: SleepWellAdapter
    private var list = arrayListOf<SleepWellAdapterData>()

    private var directionPoint: LocalDate = LocalDate.now()
    private val startLocalDate: LocalDate =
        LocalDate.now().minusDays(1)
    private val endLocalDate: LocalDate =
        LocalDate.now().plusMonths(6).plusDays(1)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSleepWellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow(ApiUrl.sleepWell)

        sleepWellAdapter = SleepWellAdapter(list)

        binding.rv.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = sleepWellAdapter
        }

        fixWeek(directionPoint)
    }

    private fun initHandler() {

        binding.apply {

            tvLastWeek.setOnClickListener {

                val localDate = directionPoint.minusWeeks(1)

                if (localDate.isAfter(startLocalDate.minusDays(6)) &&
                    localDate.isBefore(endLocalDate.plusDays(6))
                ) {
                    directionPoint = localDate
                    fixWeek(directionPoint)
                }
            }

            tvNextWeek.setOnClickListener {

                val localDate = directionPoint.plusWeeks(1)

                if (localDate.isAfter(startLocalDate.minusDays(6)) &&
                    localDate.isBefore(endLocalDate.plusDays(6))
                ) {
                    directionPoint = localDate
                    fixWeek(directionPoint)
                }
            }

            tvSelectDay.setOnClickListener { dateDialog() }

            sleepWellAdapter.itemClick = { time, list ->
                selectDialog(time, list)
            }
        }
    }

    private fun selectDialog(time: String, list: List<SleepWellWorkingDayBean>) {

        DialogUtil.instance.selectTimeDialog(
            requireActivity(),
            time,
            list,
            okClick = {

                DialogUtil.instance.checkTimeDialog(
                    requireActivity(),
                    time,
                    it,
                    okClick = { addItem(it) }
                )
            }
        )
    }

    private fun addItem(item: SleepWellWorkingDayBean) {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                bookViewModel.queryAddOrder(
                    item,
                    success = {

                        DialogUtil.instance.singleMessageDialog(
                            requireActivity(),
                            "",
                            "預約成功"

                        ) { goHome() }
                    },
                    fail = { showErrorMsgDialog(it) }
                )

                dialog.dismiss()
            }
        }
    }

    private fun fixWeek(assignDay: LocalDate) {

        val nowTime = assignDay.toString()
        val afterSeverTime = assignDay.plusDays(6).toString()
        val weekLdList = bookViewModel.SleepWellWorkingDayLD.value?.filter {
            it.workingdate.toString() in nowTime..afterSeverTime
        } ?: listOf()

        val adapterList = mutableListOf<SleepWellAdapterData>()
        var weekDay: LocalDate
        var weekNum: Int

        for (i in 0..6) {

            weekDay = assignDay.plusDays(i.toLong())
            weekNum = weekDay.dayOfWeek.value
            if (weekNum in 6..7) continue

            val dayStr = weekDay.format(
                DateTimeFormatter.ofPattern(
                    "yyyy/MM/dd\n(${fixWeekName(weekNum)})"
                )
            )

            val amList = weekLdList.filter {
                it.workingdate == weekDay.toString() &&
                        it.bookingcount.toString() < it.count.toString() &&
                        it.starttime.toString() < "12:00"
            }

            val pmList = weekLdList.filter {
                it.workingdate == weekDay.toString() &&
                        it.bookingcount.toString() < it.count.toString() &&
                        it.starttime.toString() > "12:00"
            }

            adapterList.add(SleepWellAdapterData(dayStr, amList, pmList))
        }

        list.clear()
        list.addAll(adapterList)
        sleepWellAdapter.notifyDataSetChanged()
    }

    private fun fixWeekName(num: Int): String {

        return when (num) {
            1 -> "一"
            2 -> "二"
            3 -> "三"
            4 -> "四"
            5 -> "五"
            else -> ""
        }
    }

    private fun dateDialog() {

        val dialog = DatePickerDialog(
            requireContext(), { p0, p1, p2, p3 ->

                directionPoint = LocalDate.of(p1, p2 + 1, p3)
                Log.w(TAG, "directionPoint: $directionPoint")

                fixWeek(directionPoint)
            },
            directionPoint.year,
            directionPoint.monthValue - 1,
            directionPoint.dayOfMonth
        )

        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.datePicker.maxDate = LocalDate.now()
            .plusMonths(6)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        dialog.show()
    }

    private fun initCallBack() {


    }
}