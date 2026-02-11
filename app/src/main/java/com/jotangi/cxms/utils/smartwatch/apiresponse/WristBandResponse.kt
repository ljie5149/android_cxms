package com.jotangi.cxms.utils.smartwatch.apiresponse

import android.os.Parcelable
import com.jotangi.cxms.utils.smartwatch.model.BaseWristBandResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EcgData(
    var ecgStartTime: String? = null,
    var ecgValue: String? = null,
    var hr: Int = 0,
    var dbp: Int = 0,
    var sbp: Int = 0,
    var hrv: Int = 0
) : Parcelable

@Parcelize
data class EcgResponse(
    var data: List<EcgData>? = listOf()
) : Parcelable, BaseWristBandResponse()