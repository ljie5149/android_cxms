package com.jotangi.cxms.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WatchCommonRequest(
    var memberId: String = "",
    var startTime: String = "",
    var endTime: String = "",
) : Parcelable