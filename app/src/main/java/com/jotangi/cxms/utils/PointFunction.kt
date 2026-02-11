package com.jotangi.cxms.utils

import android.util.Log
import android.view.View


private const val TAG = "PointFunction(TAG)"


/**
 * View
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/**
 * 驗證證件
 */

// 身分證
fun String.checkId() = when {

    !this.matches(Regex("^[A-Z][0-9]{9}$")) -> false

    this[0] in setOf('I', 'O') -> false

    this[1] !in setOf('1', '2') -> false

    !checkIdLastNum(this) -> false

    else -> true
}

private fun checkIdLastNum(id: String): Boolean {

    val firstNum = id[0].charCode()

    return "${firstNum}${id.substring(1, id.length)}".checkListNum()
}

// 居留證
fun String.checkRid() = when {

    !(this.matches(Regex("^[A-Z]{2}[0-9]{8}$")) ||
            this.matches(Regex("^[A-Z][0-9]{9}$"))) -> false

    this[1] !in setOf('A', 'B', 'C', 'D', '8', '9') -> false

    !checkPidLastNum(this) -> false

    else -> true
}

private fun checkPidLastNum(id: String): Boolean {

    val firstNum = id[0].charCode()

    var secondNum: Int

    id[1].also {

        secondNum = if (it in setOf('8', '9')) {
            it.toString().toInt()
        } else {
            id[1].charCode() % 10
        }
    }

    return "${firstNum}${secondNum}${id.substring(2, id.length)}".checkListNum()
}

// 共用驗證
private fun Char.charCode() = when (this) {

    in 'A'..'H' -> this.code - 55
    'I' -> 34
    in 'J'..'N' -> this.code - 56
    'O' -> 35
    in 'P'..'V' -> this.code - 57
    'W' -> 32
    in 'X'..'Y' -> this.code - 58
    'Z' -> 33

    else -> -1
}

private fun String.checkListNum(): Boolean {

    Log.w(TAG, "fixString: $this")

    if (this.length != 11) return false

    val multipleList = listOf(1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1)
    var total = 0

    this.forEachIndexed { i, c ->
        if (c == '-') return false
        total += c.toString().toInt() * multipleList[i]
    }

    Log.w(TAG, "total: $total  餘數: ${total % 10}")
    return total % 10 == 0
}