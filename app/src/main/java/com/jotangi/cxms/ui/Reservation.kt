package com.jotangi.cxms.ui

import com.google.gson.annotations.SerializedName

data class Reservation(
    @SerializedName("rid") val rid: String = "",
    @SerializedName("did") val did: String = "",
    @SerializedName("booking_no") val bookingNo: String = "",
    @SerializedName("store_id") val storeId: String = "",
    @SerializedName("reserve_date") val reserveDate: String = "",
    @SerializedName("reserve_time") val reserveTime: String = "",
    @SerializedName("reserve_endtime") val reserveEndtime: String = "",
    @SerializedName("mid") val mid: String = "",
    @SerializedName("member_phone") val memberPhone: String = "",
    @SerializedName("member_name") val memberName: String = "",
    @SerializedName("reserve_status") val reserveStatus: String = "",
    @SerializedName("pay_status") val payStatus: String = "",
    @SerializedName("pay_date") val payDate: String? = null,
    @SerializedName("reserve_created_at") val reserveCreatedAt: String = "",
    @SerializedName("store_name") val storeName: String = "",
    @SerializedName("store_picture") val storePicture: String? = null,
    @SerializedName("treatment_name") val treatmentName: String = "",
    @SerializedName("treatment_price") val treatmentPrice: String = ""
)

