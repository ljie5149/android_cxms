package com.jotangi.cxms.utils

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
            val sdf = SimpleDateFormat(DateType.YMD.key, Locale.getDefault())
            val sdfHm = SimpleDateFormat(DateType.YMDc.key, Locale.getDefault())
            val sdfe = SimpleDateFormat(DateType.W.key, Locale.getDefault())
            sdf.parse(time)?.let {
                clipTime = "${sdfHm.format(it)}(${sdfe.format(it).substring(2)})"
            }
        } catch (e: Exception) {
            e.printStackTrace()
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