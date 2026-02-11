package com.ptv.ibeacon.receiver.logger

import android.widget.ScrollView
import android.widget.TextView
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Logger {
    private var logTextView: TextView? = null
    private var scrollView: ScrollView? = null

    // Initialize the handler with TextView and ScrollView
    fun init(logTextView: TextView, scrollView: ScrollView) {
        Logger.logTextView = logTextView
        Logger.scrollView = scrollView
    }

    // Add a log message
    fun addLog(message: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        logTextView?.append("[$timestamp] $message\n")

        Timber.d("[$timestamp] $message"); // Example log statement
        scrollToBottom()
    }

    fun addRemoteLog(uuid: String, message: String) {
        Timber.d("${uuid} : ${message} : MS");
    }

    // Scroll to the bottom
    private fun scrollToBottom() {
        scrollView?.post {
            scrollView?.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}