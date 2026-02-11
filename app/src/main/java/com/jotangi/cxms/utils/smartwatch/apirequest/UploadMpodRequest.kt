package com.jotangi.cxms.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UploadMpodRequest(
    var memberId: String = "",
    var mpodStartTime: String = "",
    var lefteye: String = "",
    var righteye: String = "",
    var dataType: String = ""
) : Parcelable