package com.jotangi.cxms.utils.smartwatch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StepRequest(
    var memberId: String = "",
    var sportStartTime: String = "",
    var sportEndTime: String = "",
    var sportStep: Int = 0,
    var sportCalorie: Int = 0,
    var sportDistance: Int = 0
) : Parcelable