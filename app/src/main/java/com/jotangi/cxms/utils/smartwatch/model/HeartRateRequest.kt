package com.jotangi.cxms.utils.smartwatch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HeartRateRequest(
    var memberId: String = "",
    var heartStartTime: String = "",
    var heartEndTime: String = "",
    var heartValue: Int = 0,
    var dataType: Int = 0
) : Parcelable