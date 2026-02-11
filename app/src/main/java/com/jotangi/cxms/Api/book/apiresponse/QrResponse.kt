package com.jotangi.cxms.Api.book.apiresponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * 共用
 */
@Parcelize
open class QrDataResponse(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null
) : Parcelable

/**
 * 1.取得app會員是否是智醫城員工
 */
@Parcelize
data class IsSmartDoctorStaff(
    @SerializedName("data")
    var dataIsds: DataIsds? = null,
) : Parcelable, QrDataResponse()

@Parcelize
data class DataIsds(
    @SerializedName("isstaff")
    var isstaff: Int? = null
) : Parcelable

/**
 * 5. 取得qrcode資訊
 * (注意:app訪客, 員工, 病患皆可使用此取得)
 */
@Parcelize
data class GetQrCodeResponse(
    @SerializedName("data")
    var QrCodeData: QrCodeData = QrCodeData(),
) : Parcelable, QrDataResponse()

@Parcelize
data class QrCodeData(
    @SerializedName("qrcode")
    val qrcode: String? = null
) : Parcelable