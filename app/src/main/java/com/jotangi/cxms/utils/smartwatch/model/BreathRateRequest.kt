package com.jotangi.cxms.utils.smartwatch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BreathRateRequest(
    var memberId: String = "",
    var startTime: String = "",
    var endTime: String = "",
) : Parcelable
