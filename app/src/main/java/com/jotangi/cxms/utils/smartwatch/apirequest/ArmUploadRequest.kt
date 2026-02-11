package com.jotangi.cxms.utils.smartwatch.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ArmUploadRequest(
    var memberId: String = "",
    var bloodStartTime: String = "",
    var LbloodDBP: String = "",
    var LbloodSBP: String = "",
    var LbloodPP: String = "",
    var LbloodMAP: String = "",
    var RbloodDBP: String = "",
    var RbloodSBP: String = "",
    var RbloodPP:  String = "",
    var RbloodMAP: String = "",
    var heartValue: String = "",
    var dataType: String = "",
) : Parcelable