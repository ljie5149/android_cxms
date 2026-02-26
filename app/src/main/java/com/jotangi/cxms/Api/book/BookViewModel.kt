package com.jotangi.cxms.Api.book

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.Api.book.apirequest.HisRegistrationRequest
import com.jotangi.cxms.Api.book.apirequest.UserEditRequest
import com.jotangi.cxms.Api.book.apiresponse.BannerList2Bean
import com.jotangi.cxms.Api.book.apiresponse.ComplexRegisterListData
import com.jotangi.cxms.Api.book.apiresponse.DivisionDoctorData
import com.jotangi.cxms.Api.book.apiresponse.GetCouponDataBeen
import com.jotangi.cxms.Api.book.apiresponse.GetStoreApplyListBeen
import com.jotangi.cxms.Api.book.apiresponse.HisOplistBean
import com.jotangi.cxms.Api.book.apiresponse.HisRegistrationBean
import com.jotangi.cxms.Api.book.apiresponse.HisRegistrationListBean
import com.jotangi.cxms.Api.book.apiresponse.MemberInfoData
import com.jotangi.cxms.Api.book.apiresponse.PhysicianScheduleData
import com.jotangi.cxms.Api.book.apiresponse.SleepWellWorkingDayBean
import com.jotangi.cxms.StoreMangerUi.googlevision.StationListResponse
import com.jotangi.cxms.ui.BookingRecordData5
import com.jotangi.cxms.ui.PointTransaction
import com.jotangi.cxms.ui.home.AddBookingResponse
import com.jotangi.cxms.ui.home.WorkingDay
import com.jotangi.cxms.ui.mylittlemin.AddVideoReserveOrderRequest
import com.jotangi.cxms.ui.mylittlemin.GetWorkingDay4Request
import com.jotangi.cxms.ui.record.DataRecord
import com.jotangi.cxms.utils.Const
import com.jotangi.cxms.utils.DateTimeUtil
import org.json.JSONArray
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

class BookViewModel(var bookApiRepository: BookApiRepository) : ViewModel() {

    private val TAG = "${javaClass.simpleName}(TAG)"

    val playStoreVersion = MediatorLiveData<String>()

    val memberInfoDataList = MediatorLiveData<List<MemberInfoData>>()

    val stationListLiveData = MediatorLiveData<List<StationListData>>()
    val stationInfoLiveData = MediatorLiveData<StationInfoData>()
    val stationServiceLiveData = MediatorLiveData<List<StationServiceData>>()
    val bookingDayLiveData = MediatorLiveData<List<BookingDayData>>()
    val addBookingLiveData = MediatorLiveData<AddBookingData>()
    val bookingRecordLiveData = MediatorLiveData<List<BookingRecordData>>()
    val bookingRecordLiveData5 = MediatorLiveData<List<BookingRecordData5>>()

    val smRecordLiveData = MediatorLiveData<List<StationListResponse>>()

    val bookingInfoLiveData = MediatorLiveData<BookingInfoData>()
    val cancelBookingLiveData = MediatorLiveData<CancelBookingData>()
    val WD2LiveData = MediatorLiveData<List<WorkingDay201Data>>()
    val WD3LiveData = MediatorLiveData<List<WorkingDay201Data>>()
    val AILiveData = MediatorLiveData<List<AcInResponse>>()
    val memberPointList = MediatorLiveData<List<PointTransaction>>()


    // 醫電
    val hospitalListLiveData = MediatorLiveData<List<StationListData>>()
    val hospitalInfoLiveData = MediatorLiveData<StationInfoData>()
    val hospitalPhysicianData = MediatorLiveData<List<PhysicianListData>>()
    val physicianWorkingDayListData = MediatorLiveData<List<PhysicianResponseData>>()
    val hospitalOrderNumber = MediatorLiveData<String>()
    val videoRecordCancelData = MediatorLiveData<CommonData>()
    val addBooking2LiveData = MediatorLiveData<AddBookingData>()
    val bookingRecord2LiveData = MediatorLiveData<List<BookingRecordData>>()
    val bookingInfo2LiveData = MediatorLiveData<BookingInfoData>()
    val outpatient_infoLiveData = MediatorLiveData<BookingInfoData>()
    val cancelBooking2LiveData = MediatorLiveData<CancelBookingData>()
    val cancelBooking5LiveData = MediatorLiveData<CancelBookingData>()

    val rescheduleLiveData = MediatorLiveData<CancelBookingData>()
    val updatebooking_statusLiveData = MediatorLiveData<BaseBookResponse>()
    val messagelogLiveData = MediatorLiveData<messagelogData>()

    // 關懷
    val careAuthMessageLiveData = MediatorLiveData<String>()
    val careList = MediatorLiveData<MutableList<CareListVO>?>()
    val caredList = MediatorLiveData<MutableList<CareListVO>?>()
    val careNickNameUpdateResult = MediatorLiveData<String>()
    val careMemberDeleteResult = MediatorLiveData<String>()
    val careMemberCancelResult = MediatorLiveData<String>()
    val msgBoxList = MediatorLiveData<List<NotifyHistoryListVO>>()

    var isoK = MutableLiveData(false)
    var bmp2 = MutableLiveData<Bitmap>()
    var file2 = MutableLiveData<File>()
    var file3 = MutableLiveData<Uri>()

    // 票券
    val getCouponList = MediatorLiveData<List<GetCouponDataBeen>>()
    val getCouponPoint = MediatorLiveData<String>()

    //歷史紀錄
    val getHistoryList = MediatorLiveData<List<GetStoreApplyListBeen>>()
    val physicianListLiveData = MediatorLiveData<List<PhysicianListData>>()
    val familyListLiveData = MediatorLiveData<List<FamilyListResponse>>()
    val bookingDataCountLiveData = MediatorLiveData<List<BookingDataCountData>>()
    val oppicListLiveData = MediatorLiveData<List<OppicListData>>()
    val opmp3ListLiveData = MediatorLiveData<List<Opmp3ListData>>()
    val orderDataLiveData = MediatorLiveData<OrderDataListData>()
    val videoRecordListData = MediatorLiveData<List<VideoRecordListData>>()
    val DrugInfoBeanLD = MediatorLiveData<List<DrugInfoBean>>()
    val ItemListBeanLD = MediatorLiveData<List<ItemListBean>>()
    val CheckBeanLD = MediatorLiveData<List<CheckBean>>()
    val hospitalDivisionData = MediatorLiveData<List<DivisionListData>>()
    val physicianScheduleLD = MediatorLiveData<List<PhysicianScheduleData>>()
    val hisRegistrationLD = MediatorLiveData<HisRegistrationBean>()
    val hisRegistrationListLD = MediatorLiveData<List<HisRegistrationListBean>>()
    val hisOplistLD = MediatorLiveData<List<HisOplistBean>>()
    val divisionDoctorLD = MediatorLiveData<List<DivisionDoctorData>>()
    val BannerList2BeanLD = MediatorLiveData<List<BannerList2Bean>>()

    val SleepWellWorkingDayLD = MediatorLiveData<List<SleepWellWorkingDayBean>>()
    val ComplexRegisterListDataLD = MediatorLiveData<List<ComplexRegisterListData>>()

    val workingDayLiveData = MediatorLiveData<List<WorkingDay>>()
    val addBookingResponseLiveData = MutableLiveData<AddBookingResponse?>()

    val recordDataLiveData = MediatorLiveData<List<DataRecord>?>()
    val recordDataLiveDataII = MediatorLiveData<String?>()

    val hisRecordDataLiveData = MediatorLiveData<List<HisRecordListData>?>()
    val hisRecordDataLiveInfo = MediatorLiveData<List<HisRecordInfoData>?>()

    suspend fun queryAddOrder(
        item: SleepWellWorkingDayBean,
        success: () -> Unit,
        fail: (String) -> Unit
    ) {

//        val queryRes = bookApiRepository.sleepWellBookingList()
//
//        val queryTodayList = queryRes.list.filter {
//            it.reserveDate == item.workingdate
//        }
//        Log.w(TAG, "queryTodayList: ${queryTodayList}")
//
//        val queryBlockList = queryRes.list.filter {
//            "2023-10-00" < it.reserveDate.toString() &&
//                    it.reserveDate.toString() < "2023-11-15"
//        }
//        Log.w(TAG, "queryBlockList: ${queryBlockList}")
//        Log.w(TAG, "size: ${queryBlockList.size}")
//        Log.w(TAG, "item.workingdate: ${item.workingdate.toString()}")
//
//        when {
//
//            queryTodayList.isNotEmpty() -> {
//
//                fail("每帳號1日只能預約1次。")
//                return
//            }
//
//            "2023-10-00" < item.workingdate.toString() &&
//                    item.workingdate.toString() < "2023-11-16" &&
//                    queryBlockList.size > 2 -> {
//
//                fail("在活動區間內(10/1-11/15)每個帳號只能預約3次。")
//                return
//            }
//        }

        val addRes = bookApiRepository.addSleepWell(item)

        if (addRes.code == ApiUrl.success) {

            success()
        } else {

            fail(addRes.responseMessage.toString())
        }
    }

    /**
     * (25)	查詢舒眠館預約紀錄
     */
    suspend fun sleepWellBookingList(
        success: () -> Unit,
        fail: (String) -> Unit
    ) {

        val hrlRes = bookApiRepository.hisRegistrationList2()
        val swbRes = bookApiRepository.sleepWellBookingList()
        val hrlList = hrlRes.filter { it.是否可退掛 == "Y" }

        when {

            hrlList.isEmpty() && swbRes.list.isEmpty() ->
                fail("查無預掛資料。")

            else -> {

                val list = arrayListOf<ComplexRegisterListData>()

                hrlList.forEach {
                    list.add(
                        ComplexRegisterListData(
                            Const.dataRegister,
                            DateTimeUtil.instance.chinaToYmd(it.日期.toString()),
                            it
                        )
                    )
                }

                swbRes.list.forEach {
                    list.add(
                        ComplexRegisterListData(
                            Const.dataSleepWell,
                            it.reserveDate.toString(),
                            null,
                            it
                        )
                    )
                }

                ComplexRegisterListDataLD.postValue(list.sortedBy { it.ymd })
                Log.w(TAG, "list: ${list}")
                success()
            }
        }
    }

    suspend fun sleepWellBookingListEasy() {

        val hrlRes = bookApiRepository.hisRegistrationList2()
        val swbRes = bookApiRepository.sleepWellBookingList()
        val hrlList = hrlRes.filter { it.是否可退掛 == "Y" }

        val list = arrayListOf<ComplexRegisterListData>()

        hrlList.forEach {
            list.add(
                ComplexRegisterListData(
                    Const.dataRegister,
                    DateTimeUtil.instance.chinaToYmd(it.日期.toString()),
                    it
                )
            )
        }

        swbRes.list.forEach {
            list.add(
                ComplexRegisterListData(
                    Const.dataSleepWell,
                    it.reserveDate.toString(),
                    null,
                    it
                )
            )
        }

        ComplexRegisterListDataLD.postValue(list.sortedBy { it.ymd })
        Log.w(TAG, "list: ${list}")
    }

    /**
     * (23)	查詢舒眠館可預約時間
     */
    suspend fun sleepwellWorkingday(
        startTime: String,
        endTime: String,
        success: () -> Unit,
        fail: (String) -> Unit
    ) {

        val response = bookApiRepository.sleepwellWorkingday(startTime, endTime)

        when {

            response.code != ApiUrl.success -> {
                fail(response.responseMessage.toString())
            }

            response.list.isEmpty() -> {
                fail("並無相關資料")
            }

            else -> {

                val list = response.list.filterNot {
                    it.workingdate == LocalDate.now().toString() &&
                            it.starttime.toString() <= LocalTime.now().toString().substring(0, 5)
                }
                SleepWellWorkingDayLD.postValue(list)
                success()
            }
        }
    }


    /**
     * (13) 智醫 app 查詢 banner 資料
     */
    suspend fun bannerList2(success: () -> Unit, fail: (String) -> Unit) {

        val response = bookApiRepository.bannerList2()

        if (response.code == ApiUrl.success) {

            BannerList2BeanLD.postValue(response.list)
            success()
        } else {

            fail(response.responseMessage.toString())
        }
    }

    /**
     * (14)	HIS系統的一般網路掛號
     */
    suspend fun hisRegistration2(
        request: HisRegistrationRequest,
        success: (HisRegistrationBean) -> Unit,
        fail: (String) -> Unit
    ) {
        val response = bookApiRepository.hisRegistration2(request)

        if (response.code == "0x0200" && response.list.isNotEmpty()) {

            hisRegistrationLD.postValue(response.list[0])
            success(response.list[0])
        } else {
            fail(response.responseMessage.toString())
        }
    }

    /**
     * (15)	Tours app 查詢HIS系統的掛號資料
     */
    suspend fun hisRegistrationList2(
        success: () -> Unit,
        fail: (String) -> Unit
    ) {

        val response = bookApiRepository.hisRegistrationList2()

        if (response.isNotEmpty()) {

            val registerList = response.filter { it.是否可退掛 == "Y" }

            hisRegistrationListLD.postValue(registerList)

            if (registerList.isNotEmpty()) success() else fail("查無預掛資料。")
        } else {

            hisRegistrationListLD.postValue(listOf())
//            fail(response.responseMessage.toString())
        }
    }

    /**
     * (17)	查詢HIS系統的目前看診號
     */
    suspend fun hisOplist(
        date: String,
        success: () -> Unit,
        fail: (String) -> Unit
    ) {

        val data = bookApiRepository.hisOplist2(date)
        if (data.code == "0x0200") {

            if (data.list.isNullOrEmpty()) return

            data.list?.let {
                hisOplistLD.postValue(it)
            }
            success()

        } else {
            fail(data.responseMessage.toString())
        }
    }

    /**
     * (18)	HIS系統查詢今日的領藥資訊
     */
    suspend fun hisMedicineInfo(
        success: () -> Unit,
        fail: (String) -> Unit
    ) {
        val data = bookApiRepository.hisMedicineInfo2()
        if (data.code == "0x0200" && data.list?.isNotEmpty() == true) {
            DrugInfoBeanLD.postValue(data.list!!)
            success()
        } else {
            fail(data.responseMessage.toString())
        }
    }

    /**
     * (19)	HIS系統查詢我的藥單資訊
     */
    suspend fun hisMedicineList2(
        start: String,
        end: String,
        success: () -> Unit,
        fail: (String) -> Unit
    ) {
        val data = bookApiRepository.hisMedicineList2(start, end)
        if (data.code == "0x0200" && data.list?.isNotEmpty() == true) {
            DrugInfoBeanLD.postValue(data.list!!)
            success()
        } else {
            fail(data.responseMessage.toString())
        }
    }

    /**
     * (21)	HIS系統查詢今日檢驗科進度
     */
    suspend fun hisCheckItemStatus2() {

        val response = bookApiRepository.hisCheckItemStatus2()

        if (response.code == "0x0200" && response.list?.isNotEmpty() == true) {

            CheckBeanLD.postValue(response.list!!)
        }
    }

    /**
     * (22)	HIS系統查詢待檢項目
     */
    suspend fun hisCheckItemList2(
        success: () -> Unit,
        fail: (String) -> Unit
    ) {

        val response = bookApiRepository.hisCheckItemList2()

        if (response.code == "0x0200") {
            ItemListBeanLD.postValue(response.list)
            success()
        } else {
            fail(response.responseMessage.toString())
        }
    }

    /**
     * (25)	Tours app 查詢系統的醫院科別與醫師資料
     */
    private suspend fun divisionDoctor(skip: () -> Unit) {

        val data = bookApiRepository.divisionDoctor()

        if (data.isNotEmpty()) {

            divisionDoctorLD.postValue(
                data.filterNot { it.科別 == "特別門診" }.toSet().toList()
            )

        }
        skip()
    }


    /**
     * (26)	Tours app 查詢系統的醫院科別醫師的門診資料
     */
    suspend fun physicianScheduleDiv(
        division_name: String,
        success: () -> Unit,
        fail: (String) -> Unit
    ) {

        val data = bookApiRepository.physicianSchedule(
            division_name, null
        )

        if (data.isEmpty()) {

            fail("無資料")
        } else {

            physicianScheduleLD.postValue(data)
            success()
        }
    }

    suspend fun userEdit2AndLoading(
        request: UserEditRequest,
        fail: (String) -> Unit
    ) {
        val editResponse = bookApiRepository.userEdit2(request)

        if (editResponse.code == ApiUrl.success) {

            val loadResponse = bookApiRepository.memberInfo()

            if (loadResponse.isNotEmpty()) {

                memberInfoDataList.postValue(loadResponse)
            } else {

                fail("讀取個人資料失敗")
            }

        } else {

            fail(editResponse.responseMessage.toString())
        }
    }

    suspend fun physicianScheduleDoc(
        doctor_name: String,
        success: () -> Unit,
        fail: (String) -> Unit
    ) {

        val data = bookApiRepository.physicianSchedule(
            null, doctor_name
        )

        if (data.isEmpty()) {

            fail("無資料")
        } else {

            physicianScheduleLD.postValue(data)
            success()
        }
    }

    suspend fun getDivisionList(skip: () -> Unit) {
        val data = bookApiRepository.getDivisionList()
        if (data.isNotEmpty()) {
            hospitalDivisionData.postValue(data)
        }
        divisionDoctor(skip)
    }

    suspend fun videoRecordListCancel(sid: String, skip: () -> Unit) {
        val list = bookApiRepository.videoRecordList(sid)
        videoRecordListData.postValue(list.filter {
            it.reserve_status == "1"
        })
        skip()
    }

    // 視訊預約紀錄
    suspend fun videoRecordList(sid: String, skip: () -> Unit) {
        val list = bookApiRepository.videoRecordList(sid)
        videoRecordListData.postValue(list)
        skip()
    }

    suspend fun orderData(order: String, skip: () -> Unit) {
        val list = bookApiRepository.orderData(order)
        if (list.isNotEmpty()) {
            orderDataLiveData.postValue(list[0])
            skip()
        }
    }

    suspend fun opmp3List(order: String) {
        val list = bookApiRepository.opmp3List(order)
        if (list.isNotEmpty()) {
            opmp3ListLiveData.postValue(list)
        } else {
            opmp3ListLiveData.postValue(arrayListOf())
        }
    }

    suspend fun oppicList(order: String) {
        val list = bookApiRepository.oppicList(order)
        oppicListLiveData.postValue(list)
    }

    suspend fun bookingDataCount(order: String) {
        val response = bookApiRepository.bookingDataCount(order)
        if (response.isNotEmpty()) {
            bookingDataCountLiveData.postValue(response)
        }
    }

    suspend fun familyList(skip: () -> Unit) {
        val list = bookApiRepository.familyList()
        familyListLiveData.postValue(list)
        skip()
    }

    suspend fun getWorkingDay4(request: GetWorkingDay4Request, skip: () -> Unit) {
        val response = bookApiRepository.getWorkingDay4(request)
        response.responseMessage.let {
            if (response.code == "0x0200") {
                physicianWorkingDayListData.postValue(it)
                skip()
            }
        }
    }

    suspend fun physicianList(sid: String, did: String?, skip: () -> Unit) {
        val list = bookApiRepository.physicianList(sid, did)
        if (list.isNotEmpty()) {
            physicianListLiveData.postValue(list)
            skip()
        }
    }

    fun setPlayStoreVersion(version: String) {
        if (playStoreVersion.value != version) {
            playStoreVersion.postValue(version)
        }
    }

    // ---------------------------------------- 會員 ---------------------------------------

    suspend fun memberInfo(skip: () -> Unit) {
        val list = bookApiRepository.memberInfo()
        if (list.isNotEmpty()) {
            memberInfoDataList.postValue(list)
            skip()
        }
    }

    suspend fun getCouponList() {
        val list = bookApiRepository.getCouponList()
        if (list.isNotEmpty()) {
            getCouponList.postValue(list)
        }
    }

    suspend fun getCouponPoint(success: () -> Unit, fail: () -> Unit) {
        val data = bookApiRepository.getCouponPoint()
        if (data.code == "0x0200" && !data.Points.isNullOrBlank()) {
            getCouponPoint.postValue(data.Points!!)
            success()

        } else {

            fail()
        }
    }

    suspend fun getHistoryList(startdate: String, enddate: String) {
        val list = bookApiRepository.getHistoryList(startdate, enddate)
        if (list.isNotEmpty()) {
            getHistoryList.postValue(list)
        }
    }


    // 足壓量測
    val fpmList = MutableLiveData<JSONArray>()

    fun setFpmList(list: JSONArray) {
        fpmList.value = list
    }

    fun setok() {
        isoK.value = true
    }

    fun setnotok() {
        isoK.value = false
    }

    fun setbmp(bmp: Bitmap) {
        bmp2.value = bmp
    }

    fun setfile(file: File) {
        file2.value = file
    }

    fun seturi(file: Uri) {
        file3.value = file
    }

    suspend fun getStationList() {
        val data = bookApiRepository.getStationList()
        if (data.isNotEmpty()) {
            stationListLiveData.postValue(data)
        }
    }

    suspend fun getStationInfo(sid: String) {
        val data = bookApiRepository.getStationInfo(sid)
        if (data.isNotEmpty()) {
            stationInfoLiveData.postValue(data[0])
        }
    }

    suspend fun getStationService(sid: String) {
        val data = bookApiRepository.getStationService(sid)
        if (data.isNotEmpty()) {
            stationServiceLiveData.postValue(data)
        }
    }

    suspend fun getBookingDay(sid: String, reserve_date: String, sc: String) {
        val response = bookApiRepository.getBookingDay(sid, reserve_date, sc)
        val data = response.responseMessage
        var code = response.code
        bookingDayLiveData.postValue(data!!)
    }

    suspend fun addBooking(
        sid: String,
        reserve_date: String,
        reserve_time: String,
        service_item: String
    ) {
        val data = bookApiRepository.addBooking(sid, reserve_date, reserve_time, service_item)
        addBookingLiveData.postValue(data)
    }

    suspend fun getBookingRecord() {
        val data = bookApiRepository.getBookingList()
        if (data.isNotEmpty()) {
            bookingRecordLiveData.postValue(data)
        }
    }

    suspend fun getBookingRecord5(sid:Int, did:Int) {
        val data = bookApiRepository.getBookingList5(sid, did)
//        if (data.isNotEmpty()) {
            bookingRecordLiveData5.postValue(data)
//        }
    }

    suspend fun getBookingRecord5All(sid:Int) {
        val data = bookApiRepository.getBookingList5All(sid)
        if (data.isNotEmpty()) {
            bookingRecordLiveData5.postValue(data)
        }
    }

    suspend fun getBookingInfo(booking_no: String) {
        val data = bookApiRepository.getBookingInfo(booking_no)
        bookingInfoLiveData.postValue(data)
    }

    suspend fun getBookingInfo2(booking_no: String) {
        val data = bookApiRepository.getBookingInfo2(booking_no)
        bookingInfo2LiveData.postValue(data)
    }

    suspend fun outpatient_info(booking_no: String) {
        val data = bookApiRepository.outpatient_info(booking_no)
        outpatient_infoLiveData.postValue(data)
    }

    suspend fun cancelBooking(booking_no: String) {
        val data = bookApiRepository.cancelBooking(booking_no)
        cancelBookingLiveData.postValue(data)
    }

    suspend fun cancelBooking2(booking_no: String) {
        val data = bookApiRepository.cancelBooking2(booking_no)
        cancelBooking2LiveData.postValue(data)
    }

    suspend fun cancelBooking5(booking_no: String) {
        val data = bookApiRepository.cancelBooking5(booking_no)
        cancelBooking5LiveData.postValue(data)
    }



    suspend fun reschedule(booking_no: String) {
        val data = bookApiRepository.reschedule(booking_no)
        rescheduleLiveData.postValue(data)
    }

    suspend fun updatebooking_status(booking_no: String, no: String) {
        val data = bookApiRepository.updatebooking_status(booking_no, no)
        updatebooking_statusLiveData.postValue(data)
    }

    suspend fun getWD2(sid: String, serCo: String, sD: String) {

        val response = bookApiRepository.get_workingday2(sid, serCo, sD)
        val data = response.responseMessage
        WD2LiveData.postValue(data!!)
    }

    suspend fun getWD3(sid: String) {

        val response = bookApiRepository.get_workingday3(sid)
        val data = response.responseMessage
        WD3LiveData.postValue(data!!)
    }

    suspend fun getAI(aid: String) {

        val data = bookApiRepository.activity_info(aid)

        if (data.isNotEmpty()) {
            AILiveData.postValue(data)
        }
    }

    suspend fun getsmRecord() {
        val data = bookApiRepository.getSMList()
        if (data.isNotEmpty()) {
            smRecordLiveData.postValue(data)
        }
    }

    suspend fun getHospitalList() {
        val data = bookApiRepository.getHospitalList()
        if (data.isNotEmpty()) {
            hospitalListLiveData.postValue(data)
        }
    }

    suspend fun getHospitalInfo(sid: String) {
        val data = bookApiRepository.getHospitalInfo(sid)
        if (data.isNotEmpty()) {
            hospitalInfoLiveData.postValue(data[0])
        }
    }

    suspend fun getPhysicianList(sid: String, did: String) {
        val data = bookApiRepository.getPhysicianList(sid, did)
        if (data.isNotEmpty()) {
            hospitalPhysicianData.postValue(data)
        }
    }

    suspend fun getPhysicianWorkingDay(sid: String, pid: String, start_date: String) {
        val response = bookApiRepository.getPhysicianWorkingDay(sid, pid, start_date)
        response.responseMessage.let {
            physicianWorkingDayListData.postValue(it)
        }
    }

    suspend fun addVideoReserveOrder(
        request: AddVideoReserveOrderRequest,
        skip: (order: String) -> Unit
    ) {
        val response = bookApiRepository.addVideoReserveOrder(request)

        if (response.code == "0x0200") {
            response.responseMessage.let {
                hospitalOrderNumber.postValue(it)
                skip(it)
            }
        }
    }

    // (62)	Tours app 會員取消視訊諮詢預約資料
    suspend fun videoRecordCancel(booking_no: String) {
        val response = bookApiRepository.videoRecordCancel(booking_no)
        videoRecordCancelData.postValue(response)
    }


    suspend fun addBooking2(
        sid: String,
        name: String,
        phone: String,
        pid: String, bith: String, date: String, time: String, file: File
    ) {
        val data = bookApiRepository.addBooking2(sid, name, phone, pid, bith, date, time, file)
        addBooking2LiveData.postValue(data)
    }

    suspend fun getBookingRecord2() {
        val data = bookApiRepository.getBookingList2()
        if (data.isNotEmpty()) {
            bookingRecord2LiveData.postValue(data)
        }
    }

    suspend fun upload_messagelog(booking_no: String, txt: String) {
        val data = bookApiRepository.upload_messagelog(booking_no, txt)
        messagelogLiveData.postValue(data)
    }

    suspend fun upload_messagepic(booking_no: String, txt: File) {
        val data = bookApiRepository.upload_messagepic(booking_no, txt)
        messagelogLiveData.postValue(data)
    }


    // 關懷
    suspend fun getCareList() {
        val data = bookApiRepository.getCareList()
        if (data.isNotEmpty()) {
            careList.postValue(data as MutableList<CareListVO>?)
        } else {
            careList.postValue(null)
        }
    }

    suspend fun getCaredList() {
        val data = bookApiRepository.getCaredList()
        if (data.isNotEmpty()) {
            caredList.postValue(data as MutableList<CareListVO>?)
        } else {
            caredList.postValue(null)
        }
    }

    suspend fun addCarelist(cmemberId: String) {
        val response = bookApiRepository.addCarelist(cmemberId)
        val responseMessage = response.responseMessage
        careAuthMessageLiveData.postValue(responseMessage)
    }


    suspend fun updateCareNickName(
        nickName: String,
        cId: String
    ) {
        val response = bookApiRepository.updateCareNickName(
            nickName,
            cId
        )
        val code = response.code
        careNickNameUpdateResult.postValue(code!!)
    }

    suspend fun deleteCareMember(cId: String) {
        val response = bookApiRepository.deleteCareMember(cId)
        val code = response.code
        careMemberDeleteResult.postValue(code!!)
    }

    suspend fun cancelCaredMember(cId: String, sms: String) {
        val response = bookApiRepository.cancelCaredMember(cId, sms)
        val code = response.code
        careMemberCancelResult.postValue(code!!)
    }

    suspend fun getMsgBoxList() {
        val data = bookApiRepository.getMsgBoxList()
        if (data.isNotEmpty()) {
            msgBoxList.postValue(data)
        }
    }

    suspend fun getMemberPointList(pointType: Int) {
        val response =bookApiRepository.getMemberPointList(pointType)
       memberPointList.postValue(response)

    }

    suspend fun updateMessageBoxReadStatus(rId: String) {
        bookApiRepository.updateMsgBoxReadStatus(rId)
    }

    fun clearData() {
        stationListLiveData.postValue(listOf())
        careAuthMessageLiveData.postValue("")
        careList.postValue(mutableListOf())
        caredList.postValue(mutableListOf())
        careNickNameUpdateResult.postValue("")
        careMemberDeleteResult.postValue("")
        careMemberCancelResult.postValue("")
        msgBoxList.postValue(listOf())
    }

    suspend fun fetchWorkingDays(
        did: Int,
        startDate: String
    ) {
        try {
            val response = bookApiRepository.getWorkingDay5(did, startDate)
            if (response.status == "true") {
                Log.d("micCheckF", response.responseMessage.toString())
                workingDayLiveData.postValue(response.responseMessage)
            } else {
                Log.d("micCheckF", response.code)
                workingDayLiveData.postValue(emptyList())
            }
        } catch (e: Exception) {
            // Log or handle the error message
            val errorMessage = e.message ?: "Unknown error"
            Log.e("micCheckF", "API Error:" +e.toString())
            // Optionally post an error state to LiveData or StateFlow
            workingDayLiveData.postValue(emptyList()) // or handle with a separate error LiveData
        }
    }

    suspend fun addBooking(did:Int, reserve_date: String, reserve_time: String, reserve_endtime:String,member_phone:String,  member_name:String, success: () -> Unit, fail: (String) -> Unit) {
        val response = bookApiRepository.addBooking(did,reserve_date,reserve_time,reserve_endtime, member_phone, member_name)
        if (response.status == "true") {
            addBookingResponseLiveData.postValue(response)
            success()
        } else {
            fail(response.toString())
            addBookingResponseLiveData.postValue(null)
        }
    }

    suspend fun fetchRecordData(
        startdate: String,
        enddate: String
    ) {
        try {
            val response = bookApiRepository.getRecordData(startdate, enddate)
            if (response.status == "true") {
                Log.d("micCheckF", response.responseMessage.toString())
                recordDataLiveData.postValue(response.responseMessage)
            } else {
                Log.d("micCheckF", response.code)
                recordDataLiveData.postValue(emptyList())
            }
        } catch (e: Exception) {
            // Log or handle the error message
            val errorMessage = e.message ?: "Unknown error"
            Log.e("micCheckF", "API Error:" +e.toString())
            // Optionally post an error state to LiveData or StateFlow
            recordDataLiveData.postValue(emptyList()) // or handle with a separate error LiveData
        }
    }
    suspend fun fetchRecordDataII(
        startdate: String,
        enddate: String
    ) {
        try {
            val response = bookApiRepository.getRecordDataII(startdate, enddate)
            if (response.status == "true") {
                Log.d("micCheckF", response.responseMessage.toString())
                recordDataLiveDataII.postValue(response.responseMessage)
            } else {
                Log.d("micCheckF", response.code)
                recordDataLiveDataII.postValue(response.responseMessage)
            }
        } catch (e: Exception) {
            // Log or handle the error message
            val errorMessage = e.message ?: "Unknown error"
            Log.e("micCheckF", "API Error:" +e.toString())
            // Optionally post an error state to LiveData or StateFlow
            recordDataLiveDataII.postValue(errorMessage) // or handle with a separate error LiveData
        }
    }
    fun clearHisRecordList() {
        hisRecordDataLiveData.postValue(null)
    }
    suspend fun fetchHisRecordList() {
        try {
            clearHisRecordList()
            val response = bookApiRepository.getHisRecordList()
            if (response.status == "true") {
                Log.d("micCheckF", response.responseMessage.toString())
                hisRecordDataLiveData.postValue(response.list)
            } else {
                response.responseMessage?.let { Log.d("micCheckF", it) }
                hisRecordDataLiveData.postValue(response.list)
            }
        } catch (e: Exception) {
            // Log or handle the error message
            val errorMessage = e.message ?: "Unknown error"
            Log.e("micCheckF", "API Error:" +e.toString())
            // Optionally post an error state to LiveData or StateFlow
            hisRecordDataLiveData.postValue(null) // or handle with a separate error LiveData
        }
    }
    fun clearRecordHistoryDetail() {
        hisRecordDataLiveInfo.postValue(null)
    }
    suspend fun fetchHisRecordInfo(counter: Int) {
        clearRecordHistoryDetail()
        try {
            val response = bookApiRepository.getHisRecordInfo(counter)
            if (response.status == "true") {
                Log.d("micCheckF", response.responseMessage.toString())
                hisRecordDataLiveInfo.postValue(response.list)
            } else {
                response.responseMessage?.let { Log.d("micCheckF", it) }
                hisRecordDataLiveInfo.postValue(response.list)
            }
        } catch (e: Exception) {
            // Log or handle the error message
            val errorMessage = e.message ?: "Unknown error"
            Log.e("micCheckF", "API Error:" +e.toString())
            // Optionally post an error state to LiveData or StateFlow
            hisRecordDataLiveInfo.postValue(null) // or handle with a separate error LiveData
        }
    }
}