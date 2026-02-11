package com.jotangi.cxms.utils.smartwatch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BPRequest(
    var memberId: String = "",
    var bloodStartTime: String = "",
    var bloodEndTime: String = "",
    var bloodDBP: Int = 0,
    var bloodSBP: Int = 0,
    var dataType: Int = 0
) : Parcelable