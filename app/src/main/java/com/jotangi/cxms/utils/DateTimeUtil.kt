package com.jotangi.cxms.utils

import com.jotangi.cxms.utils.smartwatch.WatchUtils
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateTimeUtil private constructor() {

    companion object {
        val instance: DateTimeUtil = DateTimeUtil()
    }

    enum class DateType(val key: String) {
        YMDc("yyyy年MM月dd日"),
        YMD("yyyy-MM-dd"),
        HMS("HH:mm:ss"),
        HM("HH:mm"),
        W("EEEE")
    }

    fun getNowYMD(): String {
        return SimpleDateFormat(DateType.YMD.key, Locale.getDefault()).format(Date())
    }

    fun fillZero(num: Int): String {
        return if (num < 10) "0${num}" else num.toString()
    }

    fun clipHmsTohm(time: String): String {

        var clipTime = ""

        try {

            val sdfHms = SimpleDateFormat(DateType.HMS.key, Locale.getDefault())
            val sdfHm = SimpleDateFormat(DateType.HM.key, Locale.getDefault())
            sdfHms.parse(time)?.let {
                clipTime = sdfHm.format(it)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return clipTime
    }

    fun chinaDate(time: String): String {
        var clipTime = ""
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN)
            val date = inputFormat.parse(time) ?: return ""

            val calendar = Calendar.getInstance()
            calendar.time = date

            val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.TAIWAN)

            val weekDay = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "日"
                Calendar.MONDAY -> "一"
                Calendar.TUESDAY -> "二"
                Calendar.WEDNESDAY -> "三"
                Calendar.THURSDAY -> "四"
                Calendar.FRIDAY -> "五"
                Calendar.SATURDAY -> "六"
                else -> ""
            }

            clipTime = "${dateFormat.format(date)}(${weekDay.format(date)})"

        } catch (e: Exception) {
            clipTime = ""
        }

        return clipTime
    }

    fun chinaYmdDate(time: String): String {

        var clipTime = ""
        try {
            val sdf = SimpleDateFormat(DateType.YMD.key, Locale.getDefault())
            val sdfHm = SimpleDateFormat(DateType.YMDc.key, Locale.getDefault())
            sdf.parse(time)?.let {
                clipTime = "${sdfHm.format(it)}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return clipTime
    }

    fun chinaShotDate(time: String): String {

        val c = Calendar.getInstance()

        try {
            val sdf = SimpleDateFormat(DateType.YMD.key, Locale.getDefault())

            c.time = sdf.parse(time) ?: Date()
            return "${
                c.get(Calendar.YEAR) - 1911
            }${
                fillZero(c.get(Calendar.MONTH) + 1)
            }${
                fillZero(c.get(Calendar.DATE))
            }"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "no date"
    }

    fun about20Minute(start: String, end: String): String {

        return try {

            val list = end.split(":")
            var hm = LocalTime.of(list[0].toInt(), list[1].toInt())
            "${start}-${hm.minusMinutes(20)}"
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun chinaToYmd(time: String): String {
        if (time.length == 8) {
            return yyyyMMDDToyyyy_MM_DD(time)
        } else {
            return try {
                LocalDate.of(
                    time.substring(0, 3).toInt() + 1911,
                    time.substring(3, 5).toInt(),
                    time.substring(5, 7).toInt()
                ).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }
    fun yyyyMMDDToyyyy_MM_DD(time: String): String {
        val year = time.substring(0, 4).toInt()
        val month = time.substring(4, 6).toInt()
        val date = time.substring(6, 8).toInt()
        try {
            val ret = LocalDate.of(
                time.substring(0, 4).toInt(),
                time.substring(4, 6).toInt(),
                time.substring(6, 8).toInt()
            ).toString()
            return ret
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
//        return try {
//            LocalDate.of(
//                time.substring(0, 4).toInt(),
//                time.substring(4, 6).toInt(),
//                time.substring(6, 8).toInt()
//            ).toString()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            ""
//        }
    }

    fun ymdToChinaYmdw(time: String): String{
        return try {
            LocalDate.parse(time).format(
                DateTimeFormatter.ofPattern(
                    "yyyy年MM月dd日(E)"
                )
            ).replace("週","")
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}