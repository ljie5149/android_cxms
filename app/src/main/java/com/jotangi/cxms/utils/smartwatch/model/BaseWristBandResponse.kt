package com.jotangi.cxms.utils.smartwatch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class BaseWristBandResponse(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null
) : Parcelable