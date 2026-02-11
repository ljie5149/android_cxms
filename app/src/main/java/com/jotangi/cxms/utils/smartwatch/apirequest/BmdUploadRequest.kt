package com.jotangi.cxms.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BmdUploadRequest(
    var memberId: String = "",
    var startTime: String = "",
    var TScore: String = "",
    var dataType: String = ""
) : Parcelable