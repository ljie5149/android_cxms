package com.ptv.ibeacon.receiver.logger

import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber

class FirebaseLoggingTree: Timber.Tree() {
    val firebaseDatabase = FirebaseDatabase.getInstance("https://i-beacon-receiver-test-default-rtdb.asia-southeast1.firebasedatabase.app")
    val logRef = firebaseDatabase.getReference("logs")

    // Override log function to push logs to Firebase
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val logEntry = LogEntry(priority, tag, message)

        // Push log entry to Firebase Realtime Database
        logRef.push().setValue(logEntry)
    }

    // Data class to represent a log entry
    data class LogEntry(val priority: Int, val tag: String?, val message: String)
}