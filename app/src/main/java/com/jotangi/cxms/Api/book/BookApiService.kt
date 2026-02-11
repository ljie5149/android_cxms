package com.jotangi.cxms.Api.book

import com.jotangi.cxms.Api.book.apiresponse.*
import com.jotangi.cxms.StoreMangerUi.googlevision.StationListResponse
import com.jotangi.cxms.ui.AddOrderResponse
import com.jotangi.cxms.ui.BookingRecordData5
import com.jotangi.cxms.ui.PointTransaction
import com.jotangi.cxms.ui.PointsResponse
import com.jotangi.cxms.ui.Reservation
import com.jotangi.cxms.ui.home.AddBookingResponse
import com.jotangi.cxms.ui.home.WorkingDayResponse
import com.jotangi.cxms.ui.record.DataRecordResponse
import com.jotangi.cxms.ui.record.DataRecordResponseII
import okhttp3.RequestBody
import retrofit2.http.*

// 醫電 API
interface BookApiService {

    /**
     * (26)	取消舒眠館預約紀錄
     */
    @FormUrlEncoded
    @POST("add_order2.php")
    suspend fun createOrder(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("order_amount") orderAmount: Int,
        @Field("discount_amount") discountAmount: Int,
        @Field("order_pay") orderPay: Int,

        ): AddOrderResponse

    @FormUrlEncoded
    @POST("user_point.php")
    suspend fun getPoint(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
    ): PointsResponse

    @FormUrlEncoded
    @POST("getrecdata.php")
    suspend fun getRecData(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("startdate") startdate: String,
        @Field("enddate") enddate: String
        ): DataRecordResponse

    @FormUrlEncoded
    @POST("getrecdata.php")
    suspend fun getRecDataII(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("startdate") startdate: String,
        @Field("enddate") enddate: String
    ): DataRecordResponseII

    @FormUrlEncoded
    @POST("pay_list5.php")
    suspend fun getPayList(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
    ): List<Reservation>

    @FormUrlEncoded
    @POST("api/add_order2.php")
    suspend fun addOrder(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("order_amount") orderAmount: Int,
        @Field("discount_amount") discountAmount: Int,
        @Field("order_pay") orderPay: Int,
    ): AddOrderData
    @FormUrlEncoded
    @POST("sleepwell_bookingcancel.php")
    suspend fun sleepWellBookingCancel(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") pid: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") bookingNo: String,
    ): OpenBookResponse

    /**
     * (25)	查詢舒眠館預約紀錄
     */
    @FormUrlEncoded
    @POST("sleepwell_bookinglist.php")
    suspend fun sleepWellBookingList(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") pid: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("booking_startdate") start: String,
        @Field("booking_enddate") end: String,
    ): SleepWellBookingListData

    /**
     * (24)	新增舒眠館預約紀錄
     */
    @FormUrlEncoded
    @POST("add_sleepwell.php")
    suspend fun addSleepWell(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") pid: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("reserve_date") reserveDate: String,
        @Field("shiftcode") shiftCode: String,
        @Field("starttime") start: String,
        @Field("endtime") end: String,
        @Field("roomno") roomNo: String,
    ): AddSleepWellData

    /**
     * (23)	查詢舒眠館可預約時間
     */
    @FormUrlEncoded
    @POST("sleepwell_workingday.php")
    suspend fun sleepwellWorkingday(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") pid: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("start_date") start: String,
        @Field("end_date") end: String,
    ): SleepWellWorkingDayData


    /**
     * (12)	Tours app 查詢HIS系統的醫院科別醫師資料
     */
    @FormUrlEncoded
    @POST("his_physician_list.php")
    suspend fun hisPhysicianList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("division_name") division: String,
        @Field("start_date") start: String,
        @Field("end_date") end: String,
    ): List<PhysicianScheduleData>

    /**
     * (13) 智醫 app 查詢 banner 資料
     */
    @POST("banner_list2.php")
    suspend fun bannerList2(
        @Header("x-api-key") auth_token: String
    ): BannerList2Data

    /**
     * (14)	HIS系統的一般網路掛號
     */
    @FormUrlEncoded
    @POST("his_registration2.php")
    suspend fun hisRegistration2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("reg_date") regDate: String,
        @Field("schedule_id") scheduleId: String,
    ): HisRegistrationData

    /**
     * (15)	Tours app 查詢HIS系統的掛號資料
     */
    @FormUrlEncoded
    @POST("his_registration_list2.php")
    suspend fun hisRegistrationList2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
    ): HisRegistrationListData

    /**
     * (16)	Tours app HIS系統的取消一般網路掛號
     */
    @FormUrlEncoded
    @POST("his_cancel_registration2.php")
    suspend fun hisCancelRegistration2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("registration_id") registrationId: String,
    ): OpenBookResponse

    /**
     * (17)	查詢HIS系統的目前看診號
     */
    @FormUrlEncoded
    @POST("his_oplist2.php")
    suspend fun hisOplist2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("rdate") date: String,
        @Field("division_code") start: String,
    ): HisOplistData

    /**
     * (18)	HIS系統查詢今日的領藥資訊
     */
    @FormUrlEncoded
    @POST("his_medicineinfo2.php")
    suspend fun hisMedicineInfo2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String?
    ): DrugInfoData

    /**
     * (19)	HIS系統查詢我的藥單資訊
     */
    @FormUrlEncoded
    @POST("his_medicinelist2.php")
    suspend fun hisMedicineList2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("start_date") start: String,
        @Field("end_date") end: String,
    ): DrugInfoData

    /**
     * (20)	HIS系統查詢查詢待檢項目與報到取號
     */
    @FormUrlEncoded
    @POST("his_checkitem2.php")
    suspend fun hisCheckItem2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("counter_id") counterId: String,
    ): CheckItemData

    /**
     * (21)	HIS系統查詢今日檢驗科進度
     */
    @FormUrlEncoded
    @POST("his_checkitem_status2.php")
    suspend fun hisCheckItemStatus2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
    ): CheckData

    /**
     * (22)	HIS系統查詢待檢項目
     */
    @FormUrlEncoded
    @POST("his_checkitem_list2.php")
    suspend fun hisCheckItemList2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
    ): CheckItemListData

    /**
     * (24)	Tours app 查詢HIS系統的醫院排班的醫師資料
     */
    @FormUrlEncoded
    @POST("his_physician_list2.php")
    suspend fun hisPhysicianList2(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("start_date") start: String,
        @Field("end_date") end: String,
    ): List<DivisionDoctorData>

    /**
     * (25)	Tours app 查詢系統的醫院科別與醫師資料
     */
    @FormUrlEncoded
    @POST("division_doctor.php")
    suspend fun divisionDoctor(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
    ): List<DivisionDoctorData>

    /**
     * (26)	Tours app 查詢系統的醫院科別醫師的門診資料
     */
    @FormUrlEncoded
    @POST("physician_schedule.php")
    suspend fun physicianSchedule(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("division_name") division_name: String?,
        @Field("doctor_name") doctor_name: String?,
    ): List<PhysicianScheduleData>

    @FormUrlEncoded
    @POST("updatebooking_status2.php")
    suspend fun updatebookingStatus2(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") no: String,
        @Field("reserve_status") status: String
    ): BookDataResponse

    @FormUrlEncoded
    @POST("booking_info4.php")
    suspend fun orderData(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): List<OrderDataListData>

    @FormUrlEncoded
    @POST("del_opmp3.php")
    suspend fun delOpmp3(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") order: String,
        @Field("oid") oid: String,
    ): BookDataResponse

    @POST("add_opmp3.php")
    suspend fun addOpmp3(
        @Body body: RequestBody
    ): BookDataResponse

    @FormUrlEncoded
    @POST("opmp3_list.php")
    suspend fun opmp3List(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): List<Opmp3ListData>

    @FormUrlEncoded
    @POST("del_oppic.php")
    suspend fun delOppic(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") order: String,
        @Field("oid") oid: String,
    ): BookDataResponse

    @FormUrlEncoded
    @POST("oppic_list.php")
    suspend fun oppicList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): List<OppicListData>

    @POST("add_oppic.php")
    suspend fun addOppic(
        @Body body: RequestBody
    ): BookDataResponse

    @FormUrlEncoded
    @POST("booking_datacount.php")
    suspend fun bookingDataCount(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") order: String
    ): List<BookingDataCountData>

    @FormUrlEncoded
    @POST("family_list.php")
    suspend fun familyList(
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): List<FamilyListResponse>

    @FormUrlEncoded
    @POST("get_workingday4.php")
    suspend fun getWorkingDay4(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("pid") did: String,
        @Field("start_date") start_date: String
    ): PhysicianWorkingDayListData

    @FormUrlEncoded
    @POST("physician_list.php")
    suspend fun physicianList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("did") did: String?
    ): List<PhysicianListData>

    /**
     * (1)	Tours app會員註冊
     */
    @FormUrlEncoded
    @POST("user_register2.php")
    suspend fun userRegister2(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("member_name") name: String,
        @Field("member_pid") pid: String,
        @Field("member_birthday") birthday: String
    ): BookDataResponse

    /**
     * (2)	Tours app會員登入
     */
    @FormUrlEncoded
    @POST("user_login2.php")
    suspend fun userLogin2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("FCM_Token") token: String?,
        @Field("unique_id") uniqueId: String?,
    ): BookDataResponse

    @FormUrlEncoded
    @POST("get_uidpwd2.php")
    suspend fun getUidpwd2(
        @Field("unique_id") id: String
    ): GetUidpwd2Response

    /**
     * (3)	Tours app會員登出
     */
    @FormUrlEncoded
    @POST("user_logout2.php")
    suspend fun userLogout2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String
    ): BookDataResponse

    /**
     * (4)	Tours app會員變更密碼
     */
    @FormUrlEncoded
    @POST("user_changepwd2.php")
    suspend fun userChangePwd(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("old_password") pwd: String,
        @Field("new_password") token: String
    ): BookDataResponse

    /**
     * (5)	Tours app會員忘記密碼驗證
     */
    @FormUrlEncoded
    @POST("user_code2.php")
    suspend fun userCode2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
    ): BookDataResponse

    /**
     * (6)	Tours app會員忘記密碼
     */
    @FormUrlEncoded
    @POST("user_resetpwd2.php")
    suspend fun userResetPwd2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("code") code: String
    ): BookDataResponse


    /**
     * (8)	Tours app會員資料異動
     */
    @FormUrlEncoded
    @POST("user_edit2.php")
    suspend fun userEdit2(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("member_name") name: String,
        @Field("member_gender") gender: String,
        @Field("member_email") email: String,
        @Field("member_birthday") birthday: String,
        @Field("member_address") address: String?,
        @Field("member_phone") phone: String?,
        @Field("member_pid") pid: String,
    ): BookDataResponse

    /**
     * (6)	Tours app取消會員註冊
     */
    @FormUrlEncoded
    @POST("user_unregister2.php")
    suspend fun userUnregister2(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String
    ): BookDataResponse

    @FormUrlEncoded
    @POST("user_info.php")
    suspend fun memberInfo(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String
    ): List<MemberInfoData>

    @FormUrlEncoded
    @POST("coupon_list2.php")
    suspend fun getCouponList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): List<GetCouponDataBeen>

    @FormUrlEncoded
    @POST("get_member_point.php")
    suspend fun getCouponPoint(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): GetCouponPointBeen

    @FormUrlEncoded
    @POST("apply_coupon3.php")
    suspend fun getScanCoupon(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("customer_id") customerId: String,
        @Field("coupon_id") couponId: String,
        @Field("coupon_count") couponCount: String,
    ): BookDataResponse

    @FormUrlEncoded
    @POST("storeapply_list.php")
    suspend fun getHistoryList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("apply_startdate") startdate: String,
        @Field("apply_enddate") enddate: String,
    ): List<GetStoreApplyListBeen>

    @POST("user_uploadpic2.php")
    suspend fun userUploadpic2(
        @Body body: RequestBody
    ): BookDataResponse

//    @Multipart
//    @POST("user_uploadpic.php")
//    suspend fun userUploadpic(
//        @PartMap params: MutableMap<String, RequestBody?>
//    ): BookDataResponse

    @FormUrlEncoded
    @POST("station_list.php")
    suspend fun getStationList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): List<StationListData>

    @FormUrlEncoded
    @POST("station_info.php")
    suspend fun getStationInfo(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String
    ): List<StationInfoData>

    @FormUrlEncoded
    @POST("get_stationservice.php")
    suspend fun getStationService(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String
    ): List<StationServiceData>

    @FormUrlEncoded
    @POST("is_bookingday2.php")
    suspend fun getBookingDay(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("reserve_date") reserve_date: String,
        @Field("service_code") service_code: String
    ): BookingDayListData

    @FormUrlEncoded
    @POST("add_booking.php")
    suspend fun addBooking(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("reserve_date") reserve_date: String,
        @Field("reserve_time") reserve_time: String,
        @Field("service_item") service_item: String
    ): AddBookingData

    @FormUrlEncoded
    @POST("booking_list.php")
    suspend fun getBookingList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): List<BookingRecordData>

    @FormUrlEncoded
    @POST("booking_list5.php")
    suspend fun getBookingList5(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: Int,
        @Field("did") did: Int
    ): List<BookingRecordData5>

    @FormUrlEncoded
    @POST("booking_list5.php")
    suspend fun getBookingList5All(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: Int,
    ): List<BookingRecordData5>

    @FormUrlEncoded
    @POST("booking_info.php")
    suspend fun getBookingInfo(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): List<BookingInfoData>

    @FormUrlEncoded
    @POST("booking_cancel.php")
    suspend fun cancelBooking(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): CancelBookingData

    @FormUrlEncoded
    @POST("booking_cancel2.php")
    suspend fun cancelBooking2(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): CancelBookingData

    @FormUrlEncoded
    @POST("booking_cancel5.php")
    suspend fun cancelBooking5(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): CancelBookingData

    @FormUrlEncoded
    @POST("get_workingday2.php")
    suspend fun get_workingday2(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("service_code") service_code: String,
        @Field("start_date") start_date: String
    ): WorkingDay2Data


    @FormUrlEncoded
    @POST("activity_info.php")
    suspend fun activity_info(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("aid") aid: String,
    ): List<AcInResponse>

    @FormUrlEncoded
    @POST("store_bookinglist.php")
    suspend fun getSMlist(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): List<StationListResponse>

    //2022/4/29 add
    @FormUrlEncoded
    @POST("hospital_list.php")
    suspend fun getHospitalList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): List<StationListData>

    @FormUrlEncoded
    @POST("hospital_info.php")
    suspend fun getHospitalInfo(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String
    ): List<StationInfoData>

    @FormUrlEncoded
    @POST("division_list.php")
    suspend fun getDivisionList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String?
    ): List<DivisionListData>

    @FormUrlEncoded
    @POST("physician_list.php")
    suspend fun getPhysicianList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("did") did: String
    ): List<PhysicianListData>

    @FormUrlEncoded
    @POST("get_workingday4.php")
    suspend fun getPhysicianWorkingDay(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("pid") did: String,
        @Field("start_date") start_date: String
    ): PhysicianWorkingDayListData

    @FormUrlEncoded
    @POST("add_booking4.php")
    suspend fun addVideoReserveOrder(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
        @Field("did") did: String?,
        @Field("pid") pid: String,
        @Field("reserve_date") reserve_date: String,
        @Field("reserve_time") reserve_time: String,
        @Field("reserve_endtime") reserve_endtime: String,
        @Field("price") price: String,
        @Field("member_phone") member_phone: String,
        @Field("member_name") member_name: String,
        @Field("member_email") member_email: String?,
        @Field("question") question: String?,
        @Field("invoice_type") invoice_type: String,
        @Field("invoice_address") invoice_address: String
    ): CommonData

    @FormUrlEncoded
    @POST("booking_list4.php")
    suspend fun videoRecordList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String
    ): List<VideoRecordListData>

    @FormUrlEncoded
    @POST("booking_cancel4.php")
    suspend fun videoRecordCancel(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): CommonData


    @FormUrlEncoded
    @POST("outpatient_info2.php")
    suspend fun videoRecordOutpatient(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): List<VideoRecordOutpatientData>

    @POST("add_booking2.php")
    suspend fun addBooking2(
        @Body Request: RequestBody
    ): AddBookingData

    @FormUrlEncoded
    @POST("booking_list2.php")
    suspend fun getBookingList2(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): List<BookingRecordData>

    @FormUrlEncoded
    @POST("booking_info2.php")
    suspend fun getBookingInfo2(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): List<BookingInfoData>

    @FormUrlEncoded
    @POST("upload_messagelog.php")
    suspend fun upload_messagelog(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String,
        @Field("message_text") text: String,
    ): messagelogData

    @POST("upload_messagepic.php")
    suspend fun upload_messagepic(
        @Body Request: RequestBody
    ): messagelogData

    @FormUrlEncoded
    @POST("get_workingday3.php")
    suspend fun get_workingday3(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: String,
    ): WorkingDay2Data

    @FormUrlEncoded
    @POST("outpatient_info.php")
    suspend fun outpatient_info(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): List<BookingInfoData>

    @FormUrlEncoded
    @POST("reschedule.php")
    suspend fun reschedule(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String
    ): CancelBookingData

    @FormUrlEncoded
    @POST("updatebooking_status.php")
    suspend fun updatebooking_status(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("booking_no") booking_no: String,
        @Field("reserve_status") reserve_status: String
    ): BaseBookResponse


    // 關懷
    @FormUrlEncoded
    @POST("add_carelist.php")
    suspend fun addCarelist(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("cmember_id") cId: String
    ): AddCarelistData

    @FormUrlEncoded
    @POST("carelist_info.php")
    suspend fun getCareList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
    ): List<CareListVO>

    @FormUrlEncoded
    @POST("becared_list.php")
    suspend fun getCaredList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
    ): List<CareListVO>

    @FormUrlEncoded
    @POST("upd_nickname.php")
    suspend fun updateCareNickName(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("nick_name") nickName: String,
        @Field("cid") cId: String
    ): UpdateCareNickName

    @FormUrlEncoded
    @POST("del_carelist.php")
    suspend fun deleteCareMember(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("cid") cId: String
    ): DeleteCareMember

    @FormUrlEncoded
    @POST("del_becaredlist.php")
    suspend fun cancelCaredMember(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("cid") cId: String,
        @Field("sms") sms: String
    ): CancelCaredMember

    @FormUrlEncoded
    @POST("msgbox_list.php")
    suspend fun getMsgBoxList(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): List<NotifyHistoryListVO>

    @FormUrlEncoded
    @POST("msgbox_count.php")
    suspend fun getMessageBoxCount(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String
    ): MessageBoxCountVO

    @FormUrlEncoded
    @POST("upd_msgbox.php")
    suspend fun updateMessageReadStatus(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("rid") rId: String
    ): MessageBoxStatus

    @FormUrlEncoded
    @POST("member_point_list.php")
    suspend fun getMemberPointList(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("point_type") pointType: Int
    ): List<PointTransaction>

    @FormUrlEncoded
    @POST("upd_carelist.php")
    suspend fun authCareStatus(
        @Header("x-api-key") auth_token: String,
        @Field("member_id") id: String,
        @Field("member_pwd") pwd: String,
        @Field("status") status: String,
        @Field("message_from") messageFrom: String
    ): AuthCareStatus

    @FormUrlEncoded
    @POST("get_workingday5.php")
    suspend fun getWorkingDay5(
        @Header("x-api-key") authToken: String,
        @Field("member_pid") memberPid: String,
        @Field("member_pwd") memberPwd: String,
        @Field("sid") sid: Int,
        @Field("did") did: Int,
        @Field("start_date") startDate: String
    ): WorkingDayResponse

    @FormUrlEncoded
    @POST("add_booking5.php")
    suspend fun addBooking(
        @Header("x-api-key") authToken: String,
        @Field("member_pid") memberPid: String,
        @Field("member_pwd") memberPwd: String,
        @Field("sid") sid: Int,
        @Field("did") did: Int,
        @Field("reserve_date") reserveDate: String,
        @Field("reserve_time") reserveTime: String,
        @Field("reserve_endtime") reserveEndtime: String,
        @Field("member_phone") memberPhone: String,
        @Field("member_name") memberName: String
    ): AddBookingResponse

    @FormUrlEncoded
    @POST("his_medicalrecord.php")
    suspend fun getHisRecordList(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: Int?
    ): ParentHisRecordListData
    @FormUrlEncoded
    @POST("his_medicalrecordinfo.php")
    suspend fun getHisRecordInfo(
        @Header("x-api-key") auth_token: String,
        @Field("member_pid") id: String,
        @Field("member_pwd") pwd: String,
        @Field("sid") sid: Int?,
        @Field("counter") counter: Int?
    ): ParentHisRecordInfoData
}