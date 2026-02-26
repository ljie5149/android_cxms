package com.jotangi.cxms.ui.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentCalendarBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import kotlinx.coroutines.launch
import java.time.LocalDate

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : BaseFragment() {
    private lateinit var binding: FragmentCalendarBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val availableDates = mutableListOf<String>()
    companion object {
        var reserveDate:String = ""
        var reserveTime:String = ""
        var reserveEndTime:String = ""
        var memberPhone:String = ""
        var memberName:String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnReserve = view.findViewById<TextView>(R.id.btn_reserve)
        clearParam()
        btnReserve.setOnClickListener {
            if(reserveDate.isNullOrEmpty()
                || reserveTime.isNullOrEmpty()
                || reserveEndTime.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "未輸入全部參數", Toast.LENGTH_LONG).show()
            } else {
                promptReserveCheckDialog()
            }
        }



        binding.calendarView.setOnMonthChangedListener { _, date ->
            // When the user swipes to a new month
            val monthStartDate = getMonthStartDate(date.date.year, date.date.monthValue)
            fetchAvailableDatesWithLoading(monthStartDate)
        }


        setupToolBarCalendar()
//        setupSpinner(spinner)

        bookViewModel.addBookingResponseLiveData.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.status == "true") {
                    Log.d("micCheckCall", response.toString())
                } else {
                    // Show error message
                }
            }
        }


        bookViewModel.workingDayLiveData.observe(viewLifecycleOwner) { workingDays ->
            Log.d("micCheckJH", workingDays.toString())
            binding.progressBar.visibility = View.GONE // Hide progress bar when data is loaded
            binding.spinner.visibility = View.GONE // Hide progress bar when data is loaded

            if (workingDays.isNotEmpty()) {
                Log.d("micCheckkJJ", workingDays.toString())

                // Filter workingDays to include only those with non-empty timeperiod
                val today = LocalDate.now()
                val filteredDates = workingDays
                    .filter { it.timeperiod?.isNotEmpty() == true }
                    .mapNotNull { it.workingdate }
                    .filter { workingDateStr ->
                        try {
                            val workingDate = LocalDate.parse(workingDateStr)
                            !workingDate.isBefore(today) // Keep today and future dates only
                        } catch (e: Exception) {
                            Log.e("DateParseError", "Invalid date format: $workingDateStr", e)
                            false
                        }
                    }


                // Update availableDates
                availableDates.clear()
                availableDates.addAll(filteredDates)

                // Refresh the calendar decorators
                refreshDecorators()
            } else {
                Log.d("micCheckEmpty", "No working days found.")
            }

        setupToolBarCalendar()
//        setupSpinner(spinner)
    }

        lifecycleScope.launch {
            val currentMonthStartDate = getMonthStartDate()
            fetchAvailableDatesWithLoading(currentMonthStartDate)
        }
    }

    private fun promptReserveCheckDialog() {
        val title1 = "日期 : $reserveDate"
        val title2 = "預約時間 : $reserveTime"

        // Show AlertDialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("預約資訊")
            .setMessage("$title1\n$title2") // \n for line break
            .setPositiveButton("確定預約") { _, _ ->
                reserve() // Call reserve() function
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss() // Explicitly dismiss the dialog
            }
            .show()
    }


    private fun fetchAvailableDatesWithLoading(monthStartDate: String) {
        binding.progressBar.visibility = View.VISIBLE // Show progress bar
        lifecycleScope.launch {
            try {
                bookViewModel.fetchWorkingDays(ReserveFragment.did, monthStartDate)
            } finally {
                binding.progressBar.visibility = View.GONE // Hide progress bar
            }
        }
    }

    private fun getMonthStartDate(year: Int = LocalDate.now().year, month: Int = LocalDate.now().monthValue): String {
        return LocalDate.of(year, month, 1).toString() // Returns "yyyy-MM-dd"
    }

    private fun formatTimeSlots(timeSlots: List<String>): List<String> {
        return timeSlots.map { it.substringBeforeLast(":") }
    }

    fun refreshDecorators() {
        binding.calendarView.invalidateDecorators()

        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDate = date.date.toString()
            binding.spinner.visibility = View.GONE

            if (availableDates.contains(selectedDate)) {
                reserveDate = selectedDate
                // Fetch available time slots for the selected date
                val timePeriods = getTimeSlotsForDate(selectedDate)
                if (timePeriods.isNotEmpty()) {
                    updateSpinnerWithTimeSlots(timePeriods)
                } else {
                    clearSpinner()
                    Toast.makeText(requireContext(), "查無可預約時間", Toast.LENGTH_SHORT).show()
                }
            } else {
                clearSpinner()
                Toast.makeText(requireContext(), "此日期不可預約: $selectedDate", Toast.LENGTH_SHORT).show()
            }
        }

        // Decorate available dates (GREEN)
        binding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return availableDates.contains(day.date.toString())
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(Color.parseColor("#008000"))) // Available dates in green
            }
        })

        // Decorate unavailable dates (GRAY)
        binding.calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return !availableDates.contains(day.date.toString())
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(Color.GRAY)) // Unavailable dates in gray
            }
        })
    }

    private fun clearSpinner() {
        val emptyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            emptyList<String>() // Pass an empty list
        )
        emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = emptyAdapter
    }

    private fun getTimeSlotsForDate(selectedDate: String): List<TimePeriod> {
        val workingDays = bookViewModel.workingDayLiveData.value ?: return emptyList()

        val now = java.time.LocalTime.now()
        val today = LocalDate.now().toString()

        return workingDays
            .filter { it.workingdate == selectedDate && it.timeperiod?.isNotEmpty() == true }
            .flatMap { it.timeperiod!! }
            .filter { period ->
                // If it's today, filter out passed time
                if (selectedDate == today) {
                    try {
                        val startTime = java.time.LocalTime.parse(period.starttime)
                        startTime.isAfter(now)
                    } catch (e: Exception) {
                        true // In case of parsing error, keep it
                    }
                } else {
                    true // Future dates: include all time slots
                }
            }
    }


    private fun updateSpinnerWithTimeSlots(timePeriods: List<TimePeriod>) {// 1️⃣ 先建立過濾後清單（很重要）
        val fullReservation = "額滿"
        val filteredTimePeriods = timePeriods
            .filter { it.reservation_limit.toIntOrNull() ?: 0 > 0 }
        if (filteredTimePeriods.isEmpty()) {
            // 🔴 沒有可預約時段 → 隱藏 Spinner
            binding.spinner.visibility = View.GONE

        } else {
            // 🟢 有資料才顯示
            binding.spinner.visibility = View.VISIBLE
            val adapter = object : ArrayAdapter<TimePeriod>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                filteredTimePeriods
            ) {

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)

                    val item = getItem(position)
                    val startTime = item?.starttime?.substringBeforeLast(":") ?: ""

                    val limit = item?.reservation_limit?.toIntOrNull() ?: 0
                    val count = item?.count?: 0

                    if (limit == count && limit != 0) {
                        textView.text = "$startTime (額滿)"
                        textView.setTextColor(Color.RED)
                    } else {
                        textView.text = startTime
                        textView.setTextColor(Color.BLACK)
                    }

                    return view
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)

                    val item = getItem(position)
                    val startTime = item?.starttime?.substringBeforeLast(":") ?: ""

                    val limit = item?.reservation_limit?.toIntOrNull() ?: 0
                    val count = item?.count ?: 0

                    if (limit == count && limit != 0) {
                        textView.text = "$startTime (額滿)"
                        textView.setTextColor(Color.RED)
                    } else {
                        textView.text = startTime
                        textView.setTextColor(Color.BLACK)
                    }

                    return view
                }
            }
//            val adapter = ArrayAdapter(
//                requireContext(),
//                android.R.layout.simple_spinner_item,
//                filteredTimePeriods
//                    .map { it.starttime.substringBeforeLast(":") } // Show formatted starttime
//            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = adapter

            // Handle item selection
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedTimePeriod = filteredTimePeriods[position]
                    val limit = selectedTimePeriod.reservation_limit.toIntOrNull() ?: 0
                    val count = selectedTimePeriod.count ?: 0

                    if (limit == count && limit != 0) {
                        Toast.makeText(requireContext(), "此時段已額滿", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val starttime = selectedTimePeriod.starttime
                    val endtime = selectedTimePeriod.endtime
                    reserveTime = starttime
                    reserveEndTime = endtime
                    // Assign starttime and endtime to variables
                    Log.d("micCheckTime", "Start Time: $starttime, End Time: $endtime")
                    // You can now use starttime and endtime as needed
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }
        }
    }

    fun showReserveSuccessDialog(
        context: Context,
        onQueryClick: () -> Unit
    ) {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_reserve_success, null)

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()

        view.findViewById<View>(R.id.btnContinue).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnQuery).setOnClickListener {
            dialog.dismiss()
            onQueryClick()
        }

        dialog.show()
    }

    fun reserve(){
        lifecycleScope.launch {
            memberPhone = binding.edtPhone.text.toString()
            memberName = binding.edtName.text.toString()
            if(reserveDate.isNullOrEmpty()
                || reserveTime.isNullOrEmpty()
                || reserveEndTime.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "未輸入全部參數", Toast.LENGTH_LONG).show()
            } else {
                bookViewModel.addBooking(
                    ReserveFragment.did,
                    reserveDate,
                    reserveTime,
                    reserveEndTime,
                    SharedPreferencesUtil.instances.getAccountId(),
                    SharedPreferencesUtil.instances.getAccountName().toString(),
                    success = {
                        Toast.makeText(context, "預約成功!", Toast.LENGTH_SHORT)
                            .show()
//                            findNavController().navigate(
//                                CalendarFragmentDirections.actionCalendarFragmentToReserveSuccessFragment()
//                            )
                        showReserveSuccessDialog(requireContext()) {
                            findNavController().navigate(R.id.appointFragment)
                        }
                    },
                    fail = { error ->
                        Toast.makeText(context, "預約失敗: $error", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        }
    }

    private fun clearParam() {
        reserveDate = ""
        reserveTime = ""
        reserveEndTime = ""
        memberPhone = ""
        memberName = ""
    }

}