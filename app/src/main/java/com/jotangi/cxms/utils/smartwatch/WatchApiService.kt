package com.jotangi.cxms.utils.smartwatch

import com.jotangi.cxms.utils.smartwatch.apiresponse.*
import com.jotangi.cxms.utils.smartwatch.model.*
import okhttp3.RequestBody
import retrofit2.http.*

interface WatchApiService {
    /**
     * 上傳步數資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST("JTG_Upload_Steps.php")
    suspend fun stepUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): BaseWristBandResponse

    /**
     * 上傳睡眠資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST("JTG_Upload_Sleep.php")
    suspend fun sleepUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): BaseWristBandResponse

    /**
     * 上傳心率資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST("JTG_Upload_HR.php")
    suspend fun heartRateUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): BaseWristBandResponse

    /**
     * 上傳血壓資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST("JTG_Upload_BP.php")
    suspend fun bpUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): BaseWristBandResponse

    /**
     * 上傳血氧資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST("JTG_Upload_Oxygen.php")
    suspend fun oxygenUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): BaseWristBandResponse

    /**
     * 上傳ECG資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST("JTG_Upload_ECG.php")
    suspend fun ecgUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse

//    /**
//     * 上傳運動資料
//     */
//    //@Headers(Constants.HEADER_ACCEPT)
//    @Multipart
//    @POST(SmartWatchConstants.WATCH_API_SPORT_UPLOAD)
//    suspend fun sportUpload(
//        @PartMap params: MutableMap<String, RequestBody?>
//    ): WatchUploadResponse

    /**
     * 上傳體溫資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST("JTG_Upload_Temperature.php")
    suspend fun temperatureUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse

    /**
     * 上傳呼吸率資料
     */
    //@Headers(Constants.HEADER_ACCEPT)
    @Multipart
    @POST("JTG_Upload_Respiratoryrate.php")
    suspend fun respiratoryRateUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchUploadResponse


    @FormUrlEncoded
    @POST("JTG_Get_Steps.php")
    suspend fun getGetSteps(
        @Field("memberId") memberId: String,
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
    ): GetStepsData

    @FormUrlEncoded
    @POST("JTG_Get_HR.php")
    suspend fun getHeartRate(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("memberId") memberId: String
    ): HeartRateResponse

    @FormUrlEncoded
    @POST("JTG_Get_Oxygen.php")
    suspend fun getOxygen(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("memberId") memberId: String
    ): OxygenResponse

    @FormUrlEncoded
    @POST("JTG_Get_Sleep.php")
    suspend fun getSleep(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("memberId") memberId: String
    ): SleepResponse

    @FormUrlEncoded
    @POST("JTG_Get_SleepData.php")
    suspend fun getSleepDetail(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("memberId") memberId: String
    ): SleepDetailResponse

    @FormUrlEncoded
    @POST("JTG_Get_BP.php")
    suspend fun getBP(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("memberId") memberId: String
    ): BPResponse

//    @FormUrlEncoded
//    @POST("JTG_Get_Sport")
//    suspend fun getSport(
//        @Field("startTime")startTime : String,
//        @Field("endTime")endTime : String,
//        @Field("memberId")memberId : String) : SportResponse

    @FormUrlEncoded
    @POST("JTG_Get_ECG.php")
    suspend fun getECG(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("memberId") memberId: String
    ): EcgResponse

    @FormUrlEncoded
    @POST("JTG_Get_Temperature.php")
    suspend fun getTemperature(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("memberId") memberId: String
    ): TemperatureResponse

//    @FormUrlEncoded
//    @POST("JTG_Get_Respiratoryrate.php")
//    suspend fun getRespiratoryRate(
//        @Field("startTime")startTime : String,
//        @Field("endTime")endTime : String,
//        @Field("memberId")memberId : String) : RespiratoryRateResponse

    @FormUrlEncoded
    @POST("JTG_Get_Respiratoryrate.php")
    suspend fun getBreathRate(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("memberId") memberId: String
    ): BreathRateResponse

    @Multipart
    @POST("JTG_Upload_MPOD.php")
    suspend fun uploadMpod(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchCommonResponse

    @Multipart
    @POST("JTG_Upload_BMD.php")
    suspend fun bmdUpload(
        @PartMap params: MutableMap<String, RequestBody?>
    ): WatchCommonResponse

    @FormUrlEncoded
    @POST("JTG_Get_Warrantyinfo.php")
    suspend fun getWarrantyinfo(
        @Field("memberId") memberId: String,
        @Field("memberPwd") memberPwd: String,
        @Field("deviceNo") deviceNo: String,
    ): GetWarrantyinfoData

    @FormUrlEncoded
    @POST("JTG_Upload_KCAL.php")
    suspend fun uploadKcal(
        @Field("memberId") memberId: String,
        @Field("startTime") startTime: String,
        @Field("KCAL") KCAL: String,
        @Field("dataType") dataType: String,
    ): WatchCommonResponse

    @FormUrlEncoded
    @POST("JTG_Upload_BP2.php")
    suspend fun uploadBp2(
        @Field("memberId") memberId: String,
        @Field("bloodStartTime") bloodStartTime: String,
        @Field("LbloodDBP") LbloodDBP: String,
        @Field("LbloodSBP") LbloodSBP: String,
        @Field("LbloodPP") LbloodPP: String,
        @Field("LbloodMAP") LbloodMAP: String,
        @Field("RbloodDBP") RbloodDBP: String,
        @Field("RbloodSBP") RbloodSBP: String,
        @Field("RbloodPP") RbloodPP: String,
        @Field("RbloodMAP") RbloodMAP: String,
        @Field("heartValue") heartValue: String,
        @Field("dataType") dataType: String,
    ): WatchCommonResponse

    @FormUrlEncoded
    @POST("JTG_Get_KCAL.php")
    suspend fun getKcal(
        @Field("memberId") memberId: String,
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String
    ): GetKcalData

    @FormUrlEncoded
    @POST("JTG_Get_BP2.php")
    suspend fun getBp2(
        @Field("memberId") memberId: String,
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String
    ): GetBp2Data

    @FormUrlEncoded
    @POST("JTG_Get_MPOD.php")
    suspend fun getMPOD(
        @Field("memberId") memberId: String,
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String
    ): GetMpodData

    @FormUrlEncoded
    @POST("JTG_Get_BMD.php")
    suspend fun getBMD(
        @Field("memberId") memberId: String,
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String
    ): GetBmdData


}