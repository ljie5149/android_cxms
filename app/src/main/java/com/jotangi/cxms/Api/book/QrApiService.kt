package com.jotangi.cxms.Api.book

import com.jotangi.cxms.Api.book.apiresponse.GetQrCodeResponse
import com.jotangi.cxms.Api.book.apiresponse.IsSmartDoctorStaff
import com.jotangi.cxms.Api.book.apiresponse.QrDataResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface QrApiService {

    /**
     * 1. 取得app會員是否是智醫城員工
     */

    @FormUrlEncoded
    @POST("api/smcgate_isstaff.php")
    suspend fun isSmartDoctorStaff(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
    ): IsSmartDoctorStaff

    /**
     * 2. App 訪客設定qrcode資訊
     * (注意:此api 只給app 訪客使用, web 訪客以及app員工不可使用)
     */
    @FormUrlEncoded
    @POST("api/smcgate_guest_setqrcode.php")
    suspend fun setGuestQrCode(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("member_name") name: String,
        @Field("member_email") email: String,
        @Field("qrvalid_start") startTime: String,
        @Field("qrvalid_stop") endTime: String,
    ): QrDataResponse

    /**
     * 3. App 病患設定qrcode資訊
     * (注意:此api 只給app 病患使用, app 訪客以及app員工不可使用)
     */
    @FormUrlEncoded
    @POST("api/smcgate_patient_setqrcode.php")
    suspend fun setPatientQrCode(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("member_name") name: String,
        @Field("member_email") email: String,
        @Field("qrvalid_start") startTime: String,
    ): QrDataResponse

    /**
     * 5. 取得qrcode資訊
     * (注意:app訪客, 員工, 病患皆可使用此取得)
     */
    @FormUrlEncoded
    @POST("api/smcgate_getqrcode.php")
    suspend fun getQrCode(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("member_type") type: String,
    ): GetQrCodeResponse

    /**
     * 萬用qrcode
     */
    @FormUrlEncoded
    @POST("smcgate_getqrcode.php")
    suspend fun universalQrCode(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_name") pwd: String,
        @Field("member_email") email: String?,
        @Field("member_lid") lid: String
    ): GetQrCodeResponse
}