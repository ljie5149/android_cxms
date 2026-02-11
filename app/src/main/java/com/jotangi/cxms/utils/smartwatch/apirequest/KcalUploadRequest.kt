package com.jotangi.cxms.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KcalUploadRequest(
    var memberId: String = "",
    var startTime: String = "",
    var KCAL: String = "",
    var dataType: String = "",
) : Parcelable