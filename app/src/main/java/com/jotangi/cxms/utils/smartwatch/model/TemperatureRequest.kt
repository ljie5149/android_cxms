package com.jotangi.cxms.utils.smartwatch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TemperatureRequest(
    var memberId: String = "",
    var startTime: String = "",
    var temperature: Float = 0f,
    var dataType: Int = 0
) : Parcelable

