package com.jotangi.cxms.utils.smartwatch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class GetStepsData(
    var data: List<GetStepsDataBean>? = listOf()
) : Parcelable, BaseWristBandResponse()

@Parcelize
data class GetStepsDataBean(
    var sportStartTime: String? = null,
    var sportEndTime: String? = null,
    var sportStep: String? = null,
    var sportCalorie: String? = null,
    var sportDistance: String? = null
) : Parcelable

@Parcelize
data class OxygenResponse(
    var data: List<OxygenData>? = listOf()
) : Parcelable, BaseWristBandResponse()

@Parcelize
data class OxygenData(
    var startTime: String? = null,
    var OOValue: String? = null,
    var dataType: String? = null
) : Parcelable

@Parcelize
data class TemperatureResponse(
    var data: List<TemperatureData>? = listOf()
) : Parcelable, BaseWristBandResponse()

@Parcelize
data class TemperatureData(
    var startTime: String? = null,
    var temperature: String = "",
    var dataType: String? = null
) : Parcelable

//@Parcelize
//data class RespiratoryRateResponse(
//    var data: List<RespiratoryRateData>? = listOf()
//) : Parcelable, BaseWristBandResponse()
//
//@Parcelize
//data class RespiratoryRateData(
//    var startTime: String? = null,
//    var respiratoryRate: String = "0",
//    var dataType: String? = null
//) : Parcelable

@Parcelize
data class BreathRateResponse(
    var data: List<BreathRateData>? = listOf()
) : Parcelable, BaseWristBandResponse()

@Parcelize
data class BreathRateData(
    var startTime: String? = null,
    var respiratoryrate: String? = null,
    var dataType: String? = null
) : Parcelable


// ========== 8.App 取得睡眠數據 ==========
@Parcelize
data class SleepResponse(
    var data: List<SleepData>? = listOf()
) : Parcelable, BaseWristBandResponse()

@Parcelize
data class SleepData(
    var startTime: String? = null,
    var endTime: String? = null,
    var deepSleepCount: String? = null,
    var lightSleepCount: String? = null,
    var deepSleepTotal: String? = null,
    var lightSleepTotal: String? = null
) : Parcelable


// ========== 9. App 取得睡眠細節 ==========
@Parcelize
data class SleepDetailResponse(
    var data: List<SleepDetailData>? = listOf()
) : Parcelable, BaseWristBandResponse()

@Parcelize
data class SleepDetailData(
    var sleepStartTime: String? = null,
    var sleepType: String? = null,
    var sleepLen: String? = null
) : Parcelable


@Parcelize
data class HeartRateResponse(
    var data: List<HeartRateData>? = listOf()
) : Parcelable, BaseWristBandResponse()

@Parcelize
data class HeartRateData(
    var heartStartTime: String? = null,
    var heartValue: String? = null,
    var dataType: String? = null
) : Parcelable

@Parcelize
data class BPResponse(
    var data: List<BPData>? = listOf()
) : Parcelable, BaseWristBandResponse()

@Parcelize
data class BPData(
    var bloodStartTime: String? = null,
    var bloodDBP: String? = null,
    var bloodSBP: String? = null,
    var dataType: String? = null
) : Parcelable

@Parcelize
data class SportResponse(
    var data: List<SportData>? = listOf()
) : Parcelable, BaseWristBandResponse()

@Parcelize
data class SportData(
    var sportStartTime: String? = null,
    var sportType: String? = null,
    var sportStep: String? = null,
    var sportCalorie: String? = null,
    var sportDistance: String? = null
) : Parcelable