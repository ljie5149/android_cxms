package com.jotangi.cxms.utils.smartwatch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SleepRequest(
    var memberId: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var deepSleepCount: Int = 0,
    var lightSleepCount: Int = 0,
    var deepSleepTotal: Int = 0,
    var lightSleepTotal: Int = 0,
    var sleepData: String? = ""

) : Parcelable