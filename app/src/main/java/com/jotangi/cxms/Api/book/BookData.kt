package com.jotangi.cxms.Api.book

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookData(
    var title: String = "",
    var imgUrl: String = "",
    var item: String = "",
    var time: String = "",
    var name: String = "",
    var phone: String = "",
    var status: Boolean = true
) : Parcelable