package com.jotangi.cxms.utils

class AppUtil {

    private val TAG = "${javaClass.simpleName}(TAG)"

    companion object {
        val instance by lazy { AppUtil() }
    }

    fun listIsNullBlank(vararg str: String?): Boolean {
        for (strValue in str) {
            if (strValue.isNullOrBlank()) return true
        }
        return false
    }
}