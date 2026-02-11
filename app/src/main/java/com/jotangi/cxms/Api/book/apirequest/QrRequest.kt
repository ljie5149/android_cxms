package com.jotangi.cxms.Api.book.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SetGuestQrCodeRequest(
    var name: String = "",
    var email: String = "",
    var startTime: String = "",
    var endTime: String = "",
) : Parcelable