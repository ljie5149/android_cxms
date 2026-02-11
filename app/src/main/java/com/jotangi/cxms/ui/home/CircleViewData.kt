package com.jotangi.cxms.ui.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CircleData(
    var legend: String = "",
    var icon: Int = 0,
    var background: Int = 0
) : Parcelable