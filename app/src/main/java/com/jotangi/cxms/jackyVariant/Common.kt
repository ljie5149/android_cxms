package com.jotangi.cxms.jackyVariant

import java.security.MessageDigest

object Common {
    // JTGV^$($#!*\"+nowdate+\"(&$%@^@I(5375@%$#*F#\"+\"0rS8ai99SqN5PnAg
    const val Token_h       ="JTGV^$($#!*"
    const val Token_body    ="(&$%@^@I(5375@%$#*F#"
    const val Token_tail    ="0rS8ai99SqN5PnAg"
    var Token =""

    fun getToken(input: String = Token_tail): String {
        val sDate = ConvertText.getFormattedDate("", "yyyyMMdd")
        Token = "${Token_h}${sDate}${Token_body}${input}"
        println("Raw token string before hashing: [$Token]") // Add this!
        return sha256(Token)
    }

    private fun sha256(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}