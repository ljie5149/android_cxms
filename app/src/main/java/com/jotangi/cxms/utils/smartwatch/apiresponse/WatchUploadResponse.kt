package com.jotangi.cxms.utils.smartwatch.apiresponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WatchUploadResponse(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null
) : Parcelable