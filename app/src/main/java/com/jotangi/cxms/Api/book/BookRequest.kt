package com.jotangi.cxms.ui.mylittlemin

import android.os.Parcelable
import com.jotangi.cxms.jackyVariant.Common
import com.jotangi.cxms.utils.SharedPreferencesUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddVideoReserveOrderRequest(
    var sid: String = "",
    var did: String = "",
    var pid: String = "",
    var reserve_date: String = "",
    var reserve_time: String = "",
    var reserve_endtime: String = "",
    var price: String = "",
    var member_phone: String = "",
    var member_name: String = "",
    var member_email: String = "",
    var question: String = "",
    var invoice_type: String = "",
    var invoice_address: String = "",
    var doctorName: String = ""
) : Parcelable

@Parcelize
data class GetWorkingDay4Request(
    var sid: String = "",
    var pid: String = "",
    var startDate: String = ""
) : Parcelable

@Parcelize
data class BaseBookRequest(
    var auth_token: String= Common.getToken(),
    var member_id: String = SharedPreferencesUtil.instances.getAccountId(),
    var member_pwd: String = SharedPreferencesUtil.instances.getAccountPwd(),
    var member_pid: String = SharedPreferencesUtil.instances.getAccountPid()
) : Parcelable