package com.jotangi.cxms.Api.book

import android.content.ContentValues.TAG
import android.util.Log
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.Api.AppClientManager
import com.jotangi.cxms.Api.book.apirequest.HisPhysicianListRequest
import com.jotangi.cxms.Api.book.apirequest.HisRegistrationRequest
import com.jotangi.cxms.Api.book.apirequest.UserEditRequest
import com.jotangi.cxms.Api.book.apirequest.UserLoginRequest
import com.jotangi.cxms.Api.book.apirequest.UserRegister
import com.jotangi.cxms.Api.book.apiresponse.AddSleepWellData
import com.jotangi.cxms.Api.book.apiresponse.BannerList2Data
import com.jotangi.cxms.Api.book.apiresponse.BookDataResponse
import com.jotangi.cxms.Api.book.apiresponse.DivisionDoctorData
import com.jotangi.cxms.Api.book.apiresponse.GetCouponDataBeen
import com.jotangi.cxms.Api.book.apiresponse.GetCouponPointBeen
import com.jotangi.cxms.Api.book.apiresponse.GetStoreApplyListBeen
import com.jotangi.cxms.Api.book.apiresponse.GetUidpwd2Response
import com.jotangi.cxms.Api.book.apiresponse.HisOplistData
import com.jotangi.cxms.Api.book.apiresponse.HisRegistrationData
import com.jotangi.cxms.Api.book.apiresponse.HisRegistrationListBean
import com.jotangi.cxms.Api.book.apiresponse.HisRegistrationListData
import com.jotangi.cxms.Api.book.apiresponse.MemberInfoData
import com.jotangi.cxms.Api.book.apiresponse.OpenBookResponse
import com.jotangi.cxms.Api.book.apiresponse.PhysicianScheduleData
import com.jotangi.cxms.Api.book.apiresponse.SleepWellBookingListData
import com.jotangi.cxms.Api.book.apiresponse.SleepWellWorkingDayBean
import com.jotangi.cxms.Api.book.apiresponse.SleepWellWorkingDayData
import com.jotangi.cxms.StoreMangerUi.googlevision.StationListResponse
import com.jotangi.cxms.jackyVariant.Common
import com.jotangi.cxms.jackyVariant.ConvertText
import com.jotangi.cxms.ui.AddOrderResponse
import com.jotangi.cxms.ui.BookingRecordData5
import com.jotangi.cxms.ui.PointTransaction
import com.jotangi.cxms.ui.PointsResponse
import com.jotangi.cxms.ui.Reservation
import com.jotangi.cxms.ui.home.AddBookingResponse
import com.jotangi.cxms.ui.home.WorkingDayResponse
import com.jotangi.cxms.ui.mylittlemin.AddVideoReserveOrderRequest
import com.jotangi.cxms.ui.mylittlemin.BaseBookRequest
import com.jotangi.cxms.ui.mylittlemin.GetWorkingDay4Request
import com.jotangi.cxms.ui.record.DataRecordResponse
import com.jotangi.cxms.ui.record.DataRecordResponseII
import com.jotangi.cxms.utils.SharedPreferencesUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import kotlin.math.log

class BookApiRepository {

    fun toRequestBody(value: String?): RequestBody? {
        return value?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
    }

    fun toFileRequestBody(file: File?): RequestBody? {
        return file?.let { RequestBody.create("image/jpg".toMediaTypeOrNull(), it) }
//        return file?.let {
//            val uris: Uri = Uri.fromFile(it)
//            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString())
//            val mime = MimeTypeMap.getSingleton()
//                .getMimeTypeFromExtension(fileExtension.toLowerCase())
//            RequestBody.create(mime?.toMediaTypeOrNull(), it)
//        }
    }

    /**
     * (26)	取消舒眠館預約紀錄
     */
    suspend fun sleepWellBookingCancel(bookingNo: String): OpenBookResponse {

        return try {

            val baseRequest = BaseBookRequest()

            AppClientManager.instance.bookService.sleepWellBookingCancel(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                bookingNo
            )
        } catch (e: Exception) {
            OpenBookResponse()
        }
    }

    suspend fun createOrder(order_amount:Int, discount_amount:Int, order_pay:Int): AddOrderResponse {

        return try {

            val baseRequest = BaseBookRequest()
            Log.d("micCheckKKK", Common.getToken())
            AppClientManager.instance.bookService.createOrder(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                order_amount,
                discount_amount,
                order_pay

            )
        } catch (e: Exception) {
           createOrder(0, 0, 0)
        }
    }

    suspend fun getPoint(): PointsResponse {

        return try {

            val baseRequest = BaseBookRequest()
            Log.d("micCheckKKK", Common.getToken())
            AppClientManager.instance.bookService.getPoint(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            PointsResponse("", "", listOf())
        }
    }

    suspend fun getPayList(): List<Reservation> {

        return try {

            val baseRequest = BaseBookRequest()
Log.d("micCheckKKK", Common.getToken())
            AppClientManager.instance.bookService.getPayList(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            Log.d("micCheckJJJ", e.toString())
            listOf()
        }
    }

    suspend fun addOrder(orderAmount:Int, discountAmount:Int, orderPay:Int): AddOrderData {

        return try {

            val baseRequest = BaseBookRequest()

            AppClientManager.instance.bookService.addOrder(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                orderAmount,
                discountAmount,
                orderPay

            )
        } catch (e: Exception) {
            AddOrderData()
        }
    }


    /**
     * (25)	查詢舒眠館預約紀錄
     */
    suspend fun sleepWellBookingList(): SleepWellBookingListData {

        return try {

            val baseRequest = BaseBookRequest()
            val ld = LocalDate.now()

            AppClientManager.instance.bookService.sleepWellBookingList(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                ld.toString(),
                ld.plusMonths(6).toString()
            )
        } catch (e: Exception) {
            SleepWellBookingListData()
        }
    }

    /**
     * (24)	新增舒眠館預約紀錄
     */
    suspend fun addSleepWell(
        item: SleepWellWorkingDayBean
    ): AddSleepWellData {

        return try {

            val baseRequest = BaseBookRequest()

            AppClientManager.instance.bookService.addSleepWell(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                item.workingdate.toString(),
                item.shiftcode.toString(),
                item.starttime.toString(),
                item.endtime.toString(),
                item.roomno.toString()
            )
        } catch (e: Exception) {
            AddSleepWellData()
        }
    }

    /**
     * (23)	查詢舒眠館可預約時間
     */
    suspend fun sleepwellWorkingday(
        startTime: String,
        endTime: String
    ): SleepWellWorkingDayData {

        return try {

            val baseRequest = BaseBookRequest()

            AppClientManager.instance.bookService.sleepwellWorkingday(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                startTime,
                endTime,
            )
        } catch (e: Exception) {
            SleepWellWorkingDayData()
        }
    }

    /**
     * (13) 智醫 app 查詢 banner 資料
     */
    suspend fun bannerList2(): BannerList2Data {
        return try {
            AppClientManager.instance.bookService.bannerList2(Common.getToken())
        } catch (e: Exception) {
            BannerList2Data()
        }
    }

    /**
     * (14)	HIS系統的一般網路掛號
     */
    suspend fun hisRegistration2(request: HisRegistrationRequest): HisRegistrationData {
        return try {
            val baseRequest = BaseBookRequest()
            Log.d("micCheck1",  Common.getToken())
            Log.d("micCheck2",   baseRequest.member_pid)
            Log.d("micCheck3",   baseRequest.member_pwd)
            Log.d("micCheck4",   ApiUrl.c_sid)
            Log.d("micCheck5",   request.regDate)
            Log.d("micCheck6",   request.scheduleId)
            AppClientManager.instance.bookService.hisRegistration2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                request.regDate,
                request.scheduleId,
            )
        } catch (e: Exception) {
            HisRegistrationData()
        }
    }

    /**
     * (15)	Tours app 查詢HIS系統的掛號資料
     */
    suspend fun hisRegistrationList2(): List<HisRegistrationListBean> {
        return try {
            var ret: List<HisRegistrationListBean>
            val baseRequest = BaseBookRequest()
            val memberId = baseRequest.member_pid ?: ""
            val memberPwd = baseRequest.member_pwd ?: ""
            val sid = ApiUrl.c_sid ?: ""
            val auth_token = Common.getToken()
            val rawResponse = AppClientManager.instance.bookService.hisRegistrationList2Raw(
                auth_token,
                memberId,
                memberPwd,
                sid
            )

            if (rawResponse.isSuccessful) {
                val jsonString = rawResponse.body()?.string()
                val gson = Gson()
                val list: List<HisRegistrationListBean> = gson.fromJson(
                    jsonString,
                    object : TypeToken<List<HisRegistrationListBean>>() {}.type
                )
                ret = list
                Log.d("RawHisResponse", jsonString ?: "Empty response")
            } else {
                ret = emptyList()
                Log.e("RawHisResponseError", rawResponse.errorBody()?.string() ?: "Unknown error")
            }
            ret
//            AppClientManager.instance.bookService.hisRegistrationList2(
//                Common.getToken(),
//                baseRequest.member_pid,
//                baseRequest.member_pwd,
//                ApiUrl.c_sid
//            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // 發生錯誤回傳空列表
        }
    }

    /**
     * (16)	Tours app HIS系統的取消一般網路掛號
     */
    suspend fun hisCancelRegistration2(id: String): OpenBookResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.hisCancelRegistration2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                id
            )
        } catch (e: Exception) {
            OpenBookResponse()
        }
    }

    /**
     * (17)	查詢HIS系統的目前看診號
     */
    suspend fun hisOplist2(date: String): HisOplistData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.hisOplist2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                date,
                ""
            )
        } catch (e: Exception) {
            HisOplistData()
        }
    }

    /**
     * (18)	HIS系統查詢今日的領藥資訊
     */
    suspend fun hisMedicineInfo2(): DrugInfoData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.hisMedicineInfo2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid
            )
        } catch (e: Exception) {
            DrugInfoData()
        }
    }

    /**
     * (19)	HIS系統查詢我的藥單資訊
     */
    suspend fun hisMedicineList2(start: String, end: String): DrugInfoData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.hisMedicineList2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                start,
                end
            )
        } catch (e: Exception) {
            DrugInfoData()
        }
    }

    /**
     * (20)	HIS系統查詢查詢待檢項目與報到取號
     */
    suspend fun hisCheckItem2(counterId: String): CheckItemData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.hisCheckItem2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                counterId
            )
        } catch (e: Exception) {
            CheckItemData()
        }
    }

    /**
     * (21)	HIS系統查詢今日檢驗科進度
     */
    suspend fun hisCheckItemStatus2(): CheckData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.hisCheckItemStatus2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid
            )
        } catch (e: Exception) {
            CheckData()
        }
    }

    /**
     * (22)	HIS系統查詢待檢項目
     */
    suspend fun hisCheckItemList2(): CheckItemListData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.hisCheckItemList2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid
            )
        } catch (e: Exception) {
            CheckItemListData()
        }
    }

    suspend fun updatebookingStatus2(no: String): BookDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.updatebookingStatus2(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                no,
                "4"
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }

    suspend fun orderData(order: String): List<OrderDataListData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.orderData(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                order
            )
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    suspend fun delOpmp3(order: String, oid: String): BookDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.delOpmp3(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                order,
                oid
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }

    suspend fun addOpmp3(order: String, file: File): BookDataResponse {
        val baseRequest = BaseBookRequest()
        val request = MultipartBody.Builder()
            .apply {
                setType(MultipartBody.FORM)
                addFormDataPart("member_id", baseRequest.member_id)
                addFormDataPart("member_pwd", baseRequest.member_pwd)
                addFormDataPart("booking_no", order)

                val fileName = file.path.substring(
                    file.path.lastIndexOf("/") + 1
                )
                Log.d("TAG", "fileName: $fileName")
                if (file.exists()) {
                    addFormDataPart(
                        "upload_filename", fileName,
                        file.asRequestBody("audio/mp3".toMediaType())
                    )
                }
            }
        return AppClientManager.instance.bookService.addOpmp3(request.build())
    }

    suspend fun opmp3List(order: String): List<Opmp3ListData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.opmp3List(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                order
            )
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    suspend fun delOppic(order: String, oid: String): BookDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.delOppic(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                order,
                oid
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }

    suspend fun addOppic(order: String, upload_filename: String): BookDataResponse {
        val baseRequest = BaseBookRequest()
        val request = MultipartBody.Builder()
            .apply {
                setType(MultipartBody.FORM)
                addFormDataPart("member_id", baseRequest.member_id)
                addFormDataPart("member_pwd", baseRequest.member_pwd)
                addFormDataPart("booking_no", order)

                val fileName = upload_filename.substring(
                    upload_filename.lastIndexOf("/") + 1
                )
                Log.d("TAG", "fileName: $fileName")
                val file = File(upload_filename)
                if (file.exists()) {
                    Log.d("TAG", "exists: ")
                    addFormDataPart(
                        "upload_filename", fileName,
                        toFileRequestBody(file)!!
                    )
                }
            }
        return AppClientManager.instance.bookService.addOppic(request.build())
    }

    suspend fun oppicList(order: String): List<OppicListData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.oppicList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                order
            )
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    suspend fun bookingDataCount(order: String): List<BookingDataCountData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.bookingDataCount(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                order
            )
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    suspend fun familyList(): List<FamilyListResponse> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.familyList(
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    suspend fun getWorkingDay4(request: GetWorkingDay4Request): PhysicianWorkingDayListData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getWorkingDay4(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                request.sid,
                request.pid,
                request.startDate
            )
        } catch (e: Exception) {
            PhysicianWorkingDayListData()
        }
    }

    suspend fun physicianList(sid: String, did: String?): List<PhysicianListData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.physicianList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid,
                did
            )
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    /**
     * (12)	Tours app 查詢HIS系統的醫院科別醫師資料
     */
    suspend fun hisPhysicianList(request: HisPhysicianListRequest): List<PhysicianScheduleData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.hisPhysicianList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                request.division,
                request.start,
                request.end
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun hisPhysicianList2(
        start: String, end: String
    ): List<DivisionDoctorData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.hisPhysicianList2(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                start,
                end
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    /**
     * (25)	Tours app 查詢系統的醫院科別與醫師資料
     */
    suspend fun divisionDoctor(): List<DivisionDoctorData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.divisionDoctor(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid
            )
        } catch (e: Exception) {
            listOf()
        }
    }

    /**
     * (26)	Tours app 查詢系統的醫院科別醫師的門診資料
     */
    suspend fun physicianSchedule(
        division_name: String?,
        doctor_name: String?
    ): List<PhysicianScheduleData> {
        var v_startd = ""
        var v_endd = ""
        if (Common.query30day) {
            v_endd = ConvertText.getFormattedDate("")
            v_startd = ConvertText.getDateByOffset(v_endd, -30)
        }
        return try {
            val baseRequest = BaseBookRequest()
            Log.d("micCheckHH", "toekn :" + Common.getToken() + "id :" + baseRequest.member_id+ "pwd"+ baseRequest.member_pwd+"sid :" + ApiUrl.c_sid+"div :"+ division_name +"doc :" + doctor_name)
            AppClientManager.instance.bookService.physicianSchedule(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid,
                division_name,
                doctor_name,
                v_startd,
                v_endd
            )
        } catch (e: Exception) {
            listOf()
        }
    }

    /**
     * (1)	Tours app會員註冊
     */
    suspend fun userRegister2(request: UserRegister): BookDataResponse {
        return try {
            AppClientManager.instance.bookService.userRegister2(
                Common.getToken(),
                request.memberId,
                request.memberPwd,
                request.memberName,
                request.memberPid,
                request.birthday
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }


    /**
     * (2)	Tours app會員登入
     */
    suspend fun userLogin2(userLoginRequest: UserLoginRequest): BookDataResponse {
        return try {
            AppClientManager.instance.bookService.userLogin2(
                userLoginRequest.auth_token,
                userLoginRequest.memberPid,
                userLoginRequest.memberPwd,
                userLoginRequest.fcmToken,
                userLoginRequest.unique_id
            )
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            BookDataResponse()
        }
    }

    suspend fun getUidpwd2(uniqueId: String): GetUidpwd2Response {
        return try {
            AppClientManager.instance.bookService.getUidpwd2(uniqueId)
        } catch (e: Exception) {
            GetUidpwd2Response()
        }
    }

    /**
     * (3)	Tours app會員登出
     */
    suspend fun userLogout2(): BookDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.userLogout2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }

    /**
     * (4)	Tours app會員變更密碼
     */
    suspend fun userChangePwd(newPassword: String): BookDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.userChangePwd(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                newPassword
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }

    /**
     * (5)	Tours app會員忘記密碼驗證
     */
    suspend fun userCode2(memberId: String): BookDataResponse {
        return try {
            AppClientManager.instance.bookService.userCode2(
                Common.getToken(),
                memberId,
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }


    /**
     * (6)	Tours app會員忘記密碼
     */
    suspend fun userResetPwd2(pid:String, pwd:String, code:String): BookDataResponse {
        return try {
            Log.d("micCheckYUT1", code)
            Log.d("micCheckYUT2", pid)
            Log.d("micCheckYUT3", pwd)
            AppClientManager.instance.bookService.userResetPwd2(
                Common.getToken(),
               pid,
                pwd,
                code
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }

    /**
     * (6)	Tours app取消會員註冊
     */
    suspend fun userUnregister2(): BookDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.userUnregister2(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }

    suspend fun userEdit2(request: UserEditRequest): BookDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.userEdit2(
                request.auth_token,
                request.member_id,
                baseRequest.member_pwd,
                request.name,
                request.gender,
                request.email,
                request.birthday,
                request.address,
                request.phone,
                baseRequest.member_pid
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }

    suspend fun memberInfo(): List<MemberInfoData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.memberInfo(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            Log.d(TAG, "jacky ${e.toString()}")
            arrayListOf()
        }
    }

    suspend fun getCouponList(): List<GetCouponDataBeen> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getCouponList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    suspend fun getCouponPoint(): GetCouponPointBeen {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getCouponPoint(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            GetCouponPointBeen()
        }
    }

    suspend fun getScanCoupon(
        customerId: String,
        couponId: String,
        couponCount: String
    ): BookDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getScanCoupon(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                customerId,
                couponId,
                couponCount
            )
        } catch (e: Exception) {
            BookDataResponse()
        }
    }

    suspend fun getHistoryList(startdate: String, enddate: String): List<GetStoreApplyListBeen> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getHistoryList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                startdate,
                enddate
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }


    suspend fun userUploadpic2(upload_filename: String): BookDataResponse {
        val baseRequest = BaseBookRequest()
        val request = MultipartBody.Builder()
            .apply {
                setType(MultipartBody.FORM)
                addFormDataPart("member_pid", baseRequest.member_pid)
                addFormDataPart("member_pwd", baseRequest.member_pwd)
                val fileName = upload_filename.substring(
                    upload_filename.lastIndexOf("/") + 1
                )
                val file = File(upload_filename)
                if (file.exists())
                    addFormDataPart(
                        "upload_filename", fileName,
                        toFileRequestBody(file)!!
                    )
            }
        return AppClientManager.instance.bookService.userUploadpic2(request.build())
    }

    suspend fun getStationList(): List<StationListData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getStationList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getStationInfo(sid: String): List<StationInfoData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getStationInfo(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getStationService(sid: String): List<StationServiceData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getStationService(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getBookingDay(sid: String, reserve_date: String, sc: String): BookingDayListData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getBookingDay(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid,
                reserve_date,
                sc
            )
        } catch (e: Exception) {
            e.printStackTrace()
            BookingDayListData()
        }
    }

    suspend fun addBooking(
        sid: String,
        reserve_date: String,
        reserve_time: String,
        service_item: String
    ): AddBookingData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.addBooking(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid,
                reserve_date,
                reserve_time,
                service_item
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AddBookingData()
        }
    }

    suspend fun getBookingList(): List<BookingRecordData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getBookingList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getBookingList5(sid:Int,  did:Int): List<BookingRecordData5> {
        return try {
            val baseRequest = BaseBookRequest()
            Log.d("micCheckM1",  Common.getToken())
            Log.d("micCheckM2",  baseRequest.member_pid)
            Log.d("micCheckM3",  baseRequest.member_pwd)
            Log.d("micCheckM4",   sid.toString())
            Log.d("micCheckM5",   did.toString())
            AppClientManager.instance.bookService.getBookingList5(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                sid,
                did
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getBookingList5All(sid:Int): List<BookingRecordData5> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getBookingList5All(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                sid
            )
        } catch (e: Exception) {
            Log.d("micCheckLK", e.toString())
            e.printStackTrace()
            arrayListOf()
        }
    }


    suspend fun getBookingInfo(booking_no: String): BookingInfoData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getBookingInfo(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no
            )[0]
        } catch (e: Exception) {
            e.printStackTrace()
            BookingInfoData()
        }
    }

    suspend fun getBookingInfo2(booking_no: String): BookingInfoData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getBookingInfo2(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no
            )[0]
        } catch (e: Exception) {
            e.printStackTrace()
            BookingInfoData()
        }
    }

    suspend fun outpatient_info(booking_no: String): BookingInfoData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.outpatient_info(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no
            )[0]
        } catch (e: Exception) {
            // return  Result<BaseBookResponse>.isFailure()
            BookingInfoData()
        }
    }

    suspend fun cancelBooking(booking_no: String): CancelBookingData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.cancelBooking(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no
            )
        } catch (e: Exception) {
            e.printStackTrace()
            CancelBookingData()
        }
    }

    suspend fun cancelBooking2(booking_no: String): CancelBookingData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.cancelBooking2(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no
            )
        } catch (e: Exception) {
            e.printStackTrace()
            CancelBookingData()
        }
    }

    suspend fun cancelBooking5(booking_no: String): CancelBookingData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.cancelBooking5(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                booking_no
            )
        } catch (e: Exception) {
            e.printStackTrace()
            CancelBookingData()
        }
    }

    suspend fun reschedule(booking_no: String): CancelBookingData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.reschedule(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no
            )
        } catch (e: Exception) {
            e.printStackTrace()
            CancelBookingData()
        }
    }

    suspend fun updatebooking_status(booking_no: String, no: String): BaseBookResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.updatebooking_status(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no, no
            )
        } catch (e: Exception) {
            e.printStackTrace()
            BaseBookResponse()
        }
    }

    /*2022/04/08新增*/
    suspend fun get_workingday2(sid: String, serCo: String, sD: String): WorkingDay2Data {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.get_workingday2(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid,
                serCo,
                sD
            )
        } catch (e: Exception) {
            WorkingDay2Data()
        }
    }

    suspend fun get_workingday3(sid: String): WorkingDay2Data {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.get_workingday3(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid,
            )
        } catch (e: Exception) {
            WorkingDay2Data()
        }
    }

    suspend fun activity_info(aid: String): List<AcInResponse> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.activity_info(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                aid
            )
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    suspend fun getSMList(): List<StationListResponse> {
        return try {
            var baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getSMlist(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getHospitalList(): List<StationListData> {
        return try {
            var baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getHospitalList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getHospitalInfo(sid: String): List<StationInfoData> {
        return try {
            var baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getHospitalInfo(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getDivisionList(): List<DivisionListData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getDivisionList(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getPhysicianList(sid: String, did: String): List<PhysicianListData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getPhysicianList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid,
                did
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getPhysicianWorkingDay(
        sid: String,
        pid: String,
        start_date: String
    ): PhysicianWorkingDayListData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getPhysicianWorkingDay(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid,
                pid,
                start_date
            )
        } catch (e: Exception) {
            e.printStackTrace()
            PhysicianWorkingDayListData()
        }
    }

    suspend fun addVideoReserveOrder(request: AddVideoReserveOrderRequest): CommonData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.addVideoReserveOrder(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                request.sid,
                request.did,
                request.pid,
                request.reserve_date,
                request.reserve_time,
                request.reserve_endtime,
                request.price,
                request.member_phone,
                request.member_name,
                request.member_email,
                request.question,
                request.invoice_type,
                request.invoice_address
            )
        } catch (e: Exception) {
            CommonData()
        }
    }

    suspend fun videoRecordList(sid: String): List<VideoRecordListData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.videoRecordList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                sid
            )
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    suspend fun videoRecordCancel(booking_no: String): CommonData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.videoRecordCancel(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no
            )
        } catch (e: Exception) {
            e.printStackTrace()
            CommonData()
        }
    }


    suspend fun videoRecordOutpatient(booking_no: String): List<VideoRecordOutpatientData> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.videoRecordOutpatient(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun addBooking2(
        sid: String, name: String, phone: String,
        pid: String, birth: String, date: String, time: String, pic: File

    ): AddBookingData {
        Log.d("圖檔名", "${pic.name}?}")

        val multipart = MultipartBody.Builder().setType(MultipartBody.FORM)
        SharedPreferencesUtil.instances.getAccountId()
            ?.let { multipart.addFormDataPart("member_id", it) }
        SharedPreferencesUtil.instances.getAccountPwd()
            ?.let { multipart.addFormDataPart("member_pwd", it) }
        multipart.addFormDataPart("sid", sid)
        multipart.addFormDataPart("member_phone", name)
        multipart.addFormDataPart("member_name", phone)
        multipart.addFormDataPart("member_pid", pid)
        multipart.addFormDataPart("member_birthday", birth)
        multipart.addFormDataPart("reserve_date", date)
        multipart.addFormDataPart("reserve_time", time)
        multipart.addFormDataPart(
            name = "upload_filename",
            filename = pic.name,
            body = pic.asRequestBody("image/*".toMediaType())
        )
        return AppClientManager.instance.bookService.addBooking2(multipart.build())
    }

    suspend fun getBookingList2(): List<BookingRecordData> {
        return try {
            var baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getBookingList2(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun upload_messagelog(booking_no: String, txt: String): messagelogData {
        return try {
            var baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.upload_messagelog(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                booking_no, txt
            )
        } catch (e: Exception) {
            e.printStackTrace()
            messagelogData()
        }
    }

    suspend fun upload_messagepic(booking_no: String, pic: File): messagelogData {
        val multipart = MultipartBody.Builder().setType(MultipartBody.FORM)
        multipart.addFormDataPart(
            name = "upload_filename",
            filename = pic.name,
            body = pic.asRequestBody("image/*".toMediaType())
        )
        SharedPreferencesUtil.instances.getAccountId()
            ?.let { multipart.addFormDataPart("member_id", it) }
        SharedPreferencesUtil.instances.getAccountPwd()
            ?.let { multipart.addFormDataPart("member_pwd", it) }
        multipart.addFormDataPart("booking_no", booking_no)
        return AppClientManager.instance.bookService.upload_messagepic(multipart.build())
    }


    // 關懷
    suspend fun addCarelist(cId: String): AddCarelistData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.addCarelist(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                cId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AddCarelistData()
        }
    }

    // null
    suspend fun getCareList(): List<CareListVO> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getCareList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun getCaredList(): List<CareListVO> {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getCaredList(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    suspend fun updateCareNickName(
        nickName: String,
        cId: String
    ): UpdateCareNickName {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.updateCareNickName(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                nickName,
                cId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            UpdateCareNickName()
        }
    }

    suspend fun deleteCareMember(cId: String): DeleteCareMember {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.deleteCareMember(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                cId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            DeleteCareMember()
        }
    }

    suspend fun cancelCaredMember(cId: String, sms: String): CancelCaredMember {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.cancelCaredMember(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                cId,
                sms
            )
        } catch (e: Exception) {
            e.printStackTrace()
            CancelCaredMember()
        }
    }


    suspend fun getMsgBoxList(): List<NotifyHistoryListVO> {
        return try {
            val baseRequest = BaseBookRequest()
            val resp = AppClientManager.instance.bookService.getMsgBoxList(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd
            )
            resp.list!!
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    // null
    suspend fun getMessageBoxCount(): MessageBoxCountVO {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getMessageBoxCount(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd
            )
        } catch (e: Exception) {
            MessageBoxCountVO()
        }
    }

    suspend fun getMemberPointList(pointType: Int): List<PointTransaction> {
        return try {
            val baseRequest = BaseBookRequest()
            Log.d("micCheckAAS1", Common.getToken() )
            Log.d("micCheckAAS2", baseRequest.member_pid)
            Log.d("micCheckAAS3", baseRequest.member_pwd)

            AppClientManager.instance.bookService.getMemberPointList(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                pointType
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateMsgBoxReadStatus(rId: String): MessageBoxStatus {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.updateMessageReadStatus(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                rId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            MessageBoxStatus()
        }
    }

    suspend fun authCareStatus(
        status: String,
        messageFrom: String
    ): AuthCareStatus {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.authCareStatus(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                status,
                messageFrom
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AuthCareStatus()
        }
    }

    suspend fun getWorkingDay5(
        did: Int,
        startDate: String
    ): WorkingDayResponse {
        val baseRequest = BaseBookRequest()
        return try {
            val tokenHashed = Common.getToken()
            Log.d("micCheck1", tokenHashed)
            Log.d("micCheck2", baseRequest.member_pid)
            Log.d("micCheck3", baseRequest.member_pwd)
            Log.d("micCheck4", ApiUrl.c_sid.toInt().toString())
            Log.d("micCheck5",  did.toString())
            Log.d("micCheck6",  startDate.toString())
            AppClientManager.instance.bookService.getWorkingDay5(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid.toInt(),
                did,
                startDate
            )
        } catch (e: Exception) {
            Log.d("micCheckFG", e.toString())
            WorkingDayResponse("false", "0x0000", emptyList())
        }
    }

    suspend fun addBooking(did:Int, reserve_date: String, reserve_time: String, reserve_endtime:String,member_phone:String,  member_name:String  ): AddBookingResponse {
        val baseRequest = BaseBookRequest()
        return try {
            AppClientManager.instance.bookService.addBooking(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid.toInt(),
                did,
                reserve_date,
                reserve_time,
                reserve_endtime,
                member_phone,
                member_name
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AddBookingResponse("false", "0x0000", "Error: ${e.message}")
        }
    }

    suspend fun getRecordData(startdate: String, enddate: String): DataRecordResponse {
        return try {
            Log.d("jacky ", Common.getToken())
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getRecData(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                startdate,
                enddate
            )
        } catch (e: Exception) {
            e.printStackTrace()
            DataRecordResponse("false", "0x0000", emptyList())
        }
    }

    suspend fun getRecordDataII(startdate: String, enddate: String): DataRecordResponseII {
        return try {
            Log.d("jacky ", Common.getToken())
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.bookService.getRecDataII(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                startdate,
                enddate
            )
        } catch (e: Exception) {
            e.printStackTrace()
            DataRecordResponseII("false", "0x0000", "")
        }
    }
    suspend fun getHisRecordList(): ParentHisRecordListData {
        return try {
            val baseRequest = BaseBookRequest()
            val token = Common.getToken()
            val pid = baseRequest.member_pid
            val member_pwd = baseRequest.member_pwd
            val c_sid = ApiUrl.c_sid.toInt()

            Log.d("jacky ", "${Common.getToken()}")
            AppClientManager.instance.bookService.getHisRecordList(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid.toInt()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ParentHisRecordListData("false", "0x0000", "", emptyList())
        } as ParentHisRecordListData
    }
    suspend fun getHisRecordInfo(icounter: Int): ParentHisRecordInfoData {
        return try {
            val baseRequest = BaseBookRequest()
            val token = Common.getToken()
            val pid = baseRequest.member_pid
            val member_pwd = baseRequest.member_pwd
            val c_sid = ApiUrl.c_sid.toInt()

            Log.d("jacky ", "${Common.getToken()}")
            AppClientManager.instance.bookService.getHisRecordInfo(
                Common.getToken(),
                baseRequest.member_pid,
                baseRequest.member_pwd,
                ApiUrl.c_sid.toInt(),
                icounter
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ParentHisRecordInfoData("false", "0x0000", "", emptyList())
        } as ParentHisRecordInfoData
    }
}