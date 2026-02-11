package com.jotangi.cxms.utils.smartwatch.apiresponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WatchCommonResponse(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null
) : Parcelable

@Parcelize
open class OpenResponse(
    var responseMessage: String? = null,
    var status: String? = null,
    var code: String? = null
) : Parcelable


// ---------------------------------------------------------------

@Parcelize
data class GetWarrantyinfoData(
    var data: List<GetWarrantyinfoDataBean>? = arrayListOf()
) : Parcelable, OpenResponse()

@Parcelize
data class GetWarrantyinfoDataBean(
    var startTime: String? = null,
    var endTime: String? = null,
) : Parcelable


// 骨質密度
@Parcelize
data class GetBmdData(
    var data: List<GetBmdDataBean>? = arrayListOf()
) : Parcelable, OpenResponse()

@Parcelize
data class GetBmdDataBean(
    var startTime: String? = null,
    var TScore: String? = null,
    var dataType: String? = null
) : Parcelable


// 黃斑部色素
@Parcelize
data class GetMpodData(
    var data: List<GetMpodDataBean>? = arrayListOf()
) : Parcelable, OpenResponse()

@Parcelize
data class GetMpodDataBean(
    var mpodStartTime: String? = null,
    var lefteye: String? = null,
    var righteye: String? = null,
    var dataType: String? = null
) : Parcelable


@Parcelize
data class GetBp2Data(
    var data: List<GetBp2DataBean>? = arrayListOf()
) : Parcelable, OpenResponse()

@Parcelize
data class GetBp2DataBean(
    var bloodStartTime: String? = null,
    var LbloodDBP: String? = null,
    var LbloodSBP: String? = null,
    var LbloodPP: String? = null,
    var LbloodMAP: String? = null,
    var RbloodDBP: String? = null,
    var RbloodSBP: String? = null,
    var RbloodPP: String? = null,
    var RbloodMAP: String? = null,
    var heartValue: String? = null,
    var dataType: String? = null,
) : Parcelable


@Parcelize
data class GetKcalData(
    var data: List<GetKcalDataBean>? = arrayListOf()
) : Parcelable, OpenResponse()

@Parcelize
data class GetKcalDataBean(
    var startTime: String? = null,
    var KCAL: String? = null,
    var dataType: String? = null,
) : Parcelable

