package com.jotangi.cxms.utils

import android.os.Parcelable
import com.jotangi.cxms.Api.book.apiresponse.SleepWellWorkingDayBean
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SleepWellAdapterData(
    var 日期: String? = null,
    var amList: List<SleepWellWorkingDayBean> = listOf(),
    var pmList: List<SleepWellWorkingDayBean> = listOf()
) : Parcelable