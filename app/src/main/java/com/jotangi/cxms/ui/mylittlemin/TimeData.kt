package com.jotangi.cxms.ui.mylittlemin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeData(
    var date: String? = "",
    var time: String = "",
    var status: Boolean = true
) : Parcelable