package com.jotangi.cxms.ui.home

import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.jotangi.cxms.Api.book.apiresponse.PhysicianScheduleData
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.KiziCalendarDayBinding
import com.jotangi.cxms.databinding.KiziCalendarHeaderBinding
import com.jotangi.cxms.utils.DateTimeUtil
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class KiziCalendar private constructor() {

    private val TAG = "${javaClass.simpleName}(TAG)"

    companion object {
        val instance by lazy { KiziCalendar() }
    }

    interface DateListener {
        fun calendarDate(date: String)
        fun clickDate(list: List<PhysicianScheduleData>)
    }

    private lateinit var listener: DateListener
    fun setDateValue(l: DateListener) {
        listener = l
    }

    private lateinit var cv: CalendarView
    private lateinit var list: List<PhysicianScheduleData>

    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null
    private val titleFormatter = DateTimeFormatter.ofPattern("yyyy年MM月")

    fun setUpDate() {
        cv.findFirstVisibleMonth()?.also {
            cv.smoothScrollToMonth(it.yearMonth.next)
        }
    }

    fun setDownDate() {
        cv.findFirstVisibleMonth()?.also {
            cv.smoothScrollToMonth(it.yearMonth.previous)
        }
    }

    fun init(listData: List<PhysicianScheduleData>, calendarView: CalendarView) {

        list = listData
        cv = calendarView

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()

        calendarView.apply {

            setup(
                currentMonth.minusMonths(0),
                currentMonth.plusMonths(6),
                daysOfWeek.first()
            )
            scrollToMonth(currentMonth)
        }

        calendarView.post {

            Log.w(TAG, "今天 日期: $today")

            processShortDate(today.toString()).also {
                if (it.isNotEmpty()) listener.clickDate(it)
            }

            selectDate(today)
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val binding = KiziCalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {

                    Log.w(TAG, "click 日期: ${day.date}")

                    processShortDate(day.date.toString()).also {

                        if (it.isNotEmpty()) {
                            listener.clickDate(it)
                            select()
                        }
                    }
                }
            }

            private fun select() {

                if (day.owner == DayOwner.THIS_MONTH) {
                    selectDate(day.date)
                }
            }
        }

        cv.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {

                container.day = day

                val tvDay = container.binding.tvDay

                tvDay.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {

                    tvDay.makeVisible()

                    when (day.date) {

                        today -> {
                            tvDay.setTextColorRes(R.color.white)
                            tvDay.setBackgroundResource(R.drawable.kizi_today_bg)
                        }

                        selectedDate -> {
                            tvDay.setTextColorRes(R.color.blue)
                            tvDay.setBackgroundResource(R.drawable.kizi_selected_bg)
                        }

                        else -> {
                            if (processDay(day))
                                tvDay.setTextColorRes(R.color.black)
                            else
                                tvDay.setTextColorRes(R.color.gray)
                            tvDay.background = null
                        }
                    }
                } else {

                    tvDay.makeInVisible()
                }
            }
        }

        cv.monthScrollListener = {

            listener.calendarDate(titleFormatter.format(it.yearMonth))
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = KiziCalendarHeaderBinding.bind(view).includeDay.root
        }

        cv.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {

                if (container.legendLayout.tag == null) {

                    container.legendLayout.tag = data.yearMonth

                    container.legendLayout.children.map {
                        it as TextView
                    }.forEachIndexed { index, tv ->
                        tv.text = when (index) {
                            0 -> "週日"
                            1 -> "週一"
                            2 -> "週二"
                            3 -> "週三"
                            4 -> "週四"
                            5 -> "週五"
                            6 -> "週六"
                            else -> ""
                        }
                        tv.setTextColorRes(R.color.black)
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    }
                }
            }
        }
    }

    private fun processShortDate(today: String): List<PhysicianScheduleData> {

        val changeDate = DateTimeUtil.instance.chinaShotDate(today)
        Log.w(TAG, "changeDate: $changeDate")

        val itemList = ArrayList<PhysicianScheduleData>()

        list.forEach {
            if (it.日期 == changeDate) itemList.add(it)
        }

        return itemList
    }

    private fun processDay(day: CalendarDay): Boolean {

        list.forEach {

            day.date.apply {
Log.d("micCheckNB", it.日期.toString())
                val day = "${year - 1911}${fixTime(monthValue)}${fixTime(dayOfMonth)}"
                val date = it.日期.toString()
                if (day.contains(date)) {
                    return true
                }
            }
        }

        return false
    }

    private fun fixTime(num: Int): String {
        return if (num < 10) "0${num}" else "$num"
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { cv.notifyDateChanged(it) }
            cv.notifyDateChanged(date)
        }
    }
}