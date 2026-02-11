package com.jotangi.cxms.utils.smartwatch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OxygenRequest(
    var memberId: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var OOValue: Int = 0,
    var dataType: Int = 0
) : Parcelable