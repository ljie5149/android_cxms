package com.jotangi.cxms.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.util.Log
import android.widget.TextView
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import java.text.SimpleDateFormat
import java.util.*

class DialogDateUtil private constructor() {

    private val TAG = "${javaClass.simpleName}(TAG)"

    companion object {
        val instance by lazy { DialogDateUtil() }
    }

    fun qrDate(activity: Activity, tv: TextView) {

        var dateVale: String

        val calendar: Calendar = ymdToCalendar(tv.text.toString())

        val dialog = DatePickerDialog(
            activity,
            { p0, p1, p2, p3 ->

                dateVale = "${p1}-${
                    fixTimeLength("${p2 + 1}")
                }-${
                    fixTimeLength("${p3}")
                }"
                Log.d(TAG, "dateVale: $dateVale")
                tv.text = dateVale
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun fixTimeLength(str: String): String {
        return if (str.length == 1) "0$str" else str
    }

    private fun ymdToCalendar(date: String): Calendar {
        return try {
            val format = SimpleDateFormat(
                WatchUtils.DateType.YMD.key, Locale.getDefault()
            )
            val calendar = Calendar.getInstance()
            calendar.time = format.parse(date) ?: Date()
            calendar
        } catch (e: Exception) {
            e.printStackTrace()
            Calendar.getInstance()
        }
    }

    fun twoYearDate(activity: Activity, tv: TextView) {

        var dateVale: String

        val calendar: Calendar = ymdhmToCalendar(tv.text.toString())

        val dialog = DatePickerDialog(
            activity,
            { p0, p1, p2, p3 ->

                dateVale = "${p1}年${p2 + 1}月${p3}日"
                Log.d(TAG, "dateVale: $dateVale")
                tv.text = dateVale
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.maxDate = Date().time
        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, -2)
        dialog.datePicker.minDate = c.time.time
        dialog.show()
    }

    private fun ymdhmToCalendar(date: String): Calendar {
        return try {
            val format = SimpleDateFormat(
                WatchUtils.DateType.Y_M_D_CHINA.key, Locale.getDefault()
            )
            val calendar = Calendar.getInstance()
            calendar.time = format.parse(date) ?: Date()
            calendar
        } catch (e: Exception) {
            e.printStackTrace()
            Calendar.getInstance()
        }
    }
}