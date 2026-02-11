package com.jotangi.cxms.jackyVariant

import android.os.Build
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

object ConvertText {
    fun resizeByteArray(originalArray: ByteArray, newSize: Int): ByteArray {
        val newArray = ByteArray(newSize)
        val length = minOf(originalArray.size, newSize)
        System.arraycopy(originalArray, 0, newArray, 0, length)
        return newArray
    }
    fun asciiToHex(ascii: String): String {
        val result = StringBuilder()
        var i=0
        for (char in ascii) {
            val hex = Integer.toHexString(char.toInt())
            if (i > 0)
                result.append(",$hex")
            else
                result.append("$hex")
            i++
        }
        return result.toString()
    }
    fun getRandomChar(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
    fun generateRandomId(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }
    fun convertByteToHex(byte: Byte): String
    {
        var hex=""

        // Iterating through each byte in the array
        hex += String.format(" %02X", byte)
        return hex;
    }
    fun convertByteToHexadecimal(byteArray: ByteArray): String
    {
        var hex=""

        // Iterating through each byte in the array
        for (i in byteArray) {
            hex += String.format(" %02X", i)
        }
        return hex;
    }
    fun convertByteToHexadecimal4View(byteArray: ByteArray): String
    {
        var hex=""

        // Iterating through each byte in the array
        var idx=0
        for (i in byteArray) {
            if (idx == 0) {
                hex += String.format(" %02X", i)
            } else {
                hex += String.format(",%02X", i)
            }
            idx++
            if (idx % 40 == 0) hex+="\n"
            else if (idx % 10 == 0) hex+="     "
        }
        return hex;
    }
    // Function to check if the input string is a valid hexadecimal number
    fun isValidHexadecimal(input: String): Boolean {
        try {
            Integer.parseInt(input, 16)
        } catch (e: NumberFormatException) {
            return false
        }
        return true
    }
    fun isValidHexadecimal2(input: String): Boolean {
        val regex = Regex("[0-9A-Fa-f,]*")
        return input.matches(regex)
    }
    fun convertStringToByteArray(input: String, LRC: Int=0): ByteArray {
        var outArray = input.split(",")
        var outCmd=ByteArray(outArray.size + LRC)
        for (i in 0 until outArray.size) {
            var intValue = outArray[i].toInt(16)
            var byteValue = intValue.toByte()
            outCmd[i] = byteValue
        }
        return outCmd
    }
    fun convertAmountStringToByteArray(input: String): ByteArray {
        var outCmd=ByteArray(input.length)
        for (i in 0 until input.length) {
            val curValue = "3" + input.substring(i, i + 1)
            var intValue = curValue.toInt(16)
            var byteValue = intValue.toByte()
            outCmd[i] = byteValue
        }
        return outCmd
    }
    fun fmtPaymentAmount(input: Int, pattern: String="%010d"): String {
        val amount = input.toString()
        var formatAmount = pattern.format(amount.toInt())
        formatAmount += "00"
        println("jacky amount :$formatAmount")
        val byteArray= convertAmountStringToByteArray(formatAmount)
        val hex= convertByteToHexadecimal(byteArray)
        println("jacky amount Hex:$hex")
        return formatAmount
    }
    fun convertDateStringToByteArray(input: String): ByteArray {
        var outCmd=ByteArray(input.length)
        for (i in 0 until input.length) {
            val curValue = "3" + input.substring(i, i + 1)
            var intValue = curValue.toInt(16)
            var byteValue = intValue.toByte()
            outCmd[i] = byteValue
        }
        return outCmd
    }
    fun getFormattedDate(input_date: String, pattern: String="yyyy-MM-dd"): String
    {
        var date_str: String=""
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var date = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern(pattern)
                date_str = date.format(formatter)
//                println("david date_str:$date_str")
                if (input_date.isNotEmpty()) {
                    date = LocalDateTime.parse(input_date, formatter)
                    val formatter = DateTimeFormatter.ofPattern(pattern)
                    date_str = date.format(formatter)
                }
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        } catch (e: Exception) {}
        return date_str
    }fun getTaiwanDate(input_date: String, contact_symbol: String = "-"): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                // 1. 判斷輸入日期格式（有無時間）
                val localDateTime = try {
                    LocalDateTime.parse(input_date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                } catch (e: Exception) {
                    // 若只有 yyyy-MM-dd
                    LocalDate.parse(
                        input_date,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    ).atStartOfDay()
                }

                // 2. 取得民國年 (西元年 - 1911)
                val rocYear = localDateTime.year - 1911
                val month = localDateTime.monthValue
                val day = localDateTime.dayOfMonth

                // 3. 回傳你要的格式
                "${rocYear}${contact_symbol}${month}${contact_symbol}${day}"
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
    fun rocToLocalDate(roc: String): LocalDate {
        val rocYear = roc.substring(0, 3).toInt()
        val month = roc.substring(3, 5).toInt()
        val day = roc.substring(5, 7).toInt()
        val year = rocYear + 1911
        return LocalDate.of(year, month, day)
    }
    fun rocDashToLocalDateSymbolDesh(roc: String): LocalDate {
        val parts = roc.split("-")
        val rocYear = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        val year = rocYear + 1911
        return LocalDate.of(year, month, day)
    }
    fun getDateBefore(dateStr: String, days: Int): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateStr) ?: return ""
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DATE, days)
        }
        return format.format(calendar.time)
    }
    fun getHours(start: String, end: String): Long {
        var ret: Long=0
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val dateTimeStart = LocalDateTime.parse(start, formatter)
                val dateTimeEnd = LocalDateTime.parse(end, formatter)
                val duration = Duration.between(dateTimeStart, dateTimeEnd)

                ret = duration.toHours()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        } catch (e: Exception) {}
        return ret
    }
    fun getMinutes(start: String, end: String): Long {
        var ret: Long=0
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val dateTimeStart = LocalDateTime.parse(start, formatter)
                val dateTimeEnd = LocalDateTime.parse(end, formatter)
                val duration = Duration.between(dateTimeStart, dateTimeEnd)

                val hours = duration.toHours()
                ret = duration.toMinutes()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        } catch (e: Exception) {}
        return ret
    }

    fun isNumeric(input: String): Boolean {
        val regex = """^-?(\d+(\.\d+)?|\.\d+)$""".toRegex()
        return regex.matches(input)
    }
    fun clearByteArray(originalArray: ByteArray): ByteArray {
        val newSize = originalArray.size
        return ByteArray(newSize) { 0 }
    }
}