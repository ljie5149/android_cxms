package com.jotangi.cxms.Api.book

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderDataListData(
    var booking_no: String? = null,
    var store_id: String? = null,
    var reserve_date: String? = null,
    var reserve_time: String? = null,
    var reserve_endtime: String? = null,
    var mid: String? = null,
    var member_phone: String? = null,
    var member_name: String? = null,
    var member_email: String? = null,
    var question: String? = null,
    var reserve_status: String? = null,
    var reserve_created_at: String? = null,
    var doctor_name: String? = null,
    var job_title: String? = null,
    var physician_picture: String? = null,
    var physician_status: String? = null,
    var pay_type: String? = null,
    var pay_status: String? = null,
    var price: String? = null,
    var invoice_type: String? = null,
    var invoice_address: String? = null,
) : Parcelable

@Parcelize
data class Opmp3ListData(
    var oid: String? = null,
    var member_id: String? = null,
    var booking_no: String? = null,
    var upload_date: String? = null,
    var op_mp3: String? = null,
) : Parcelable

@Parcelize
data class FamilyListResponse(
    var fid: String? = null,
    var family_name: String? = null,
    var family_type: String? = null,
    var family_gender: String? = null,
    var family_birthday: String? = null,
) : Parcelable

@Parcelize
data class BookingDataCountData(
    var outpatient_picture: String? = null,
    var outpatient_audio: String? = null,
    var questionnaire: String? = null,
) : Parcelable

@Parcelize
data class StationServiceData(
    var xid: String? = null,
    var store_id: String? = null,
    var hid: String? = null,
    var service_code: String? = null,
    var service_name: String? = null,
    var service_time: String? = null,
    var service_time2: String? = null,

    var service_price: String? = null,
    var service_status: String? = null,
    var isChecked: Int? = 0
) : Parcelable

@Parcelize
data class OppicListData(
    var oid: String? = null,
    var member_id: String? = null,
    var booking_no: String? = null,
    var upload_date: String? = null,
    var op_pic: String? = null,
) : Parcelable

@Parcelize
data class StationInfoData(
    var sid: String? = null,
    var store_id: String? = null,
    var store_type: String? = null,
    var store_name: String? = null,
    var shopping_area: String? = null,
    var store_phone: String? = null,
    var store_address: String? = null,
    var store_website: String? = null,
    var store_facebook: String? = null,
    var store_news: String? = null,
    var store_descript: String? = null,
    var store_opentime: String? = null,
    var store_picture: String? = null,
    var store_latitude: String? = null,
    var store_longitude: String? = null,
    var store_status: String? = null,
    var time_period: String? = null
) : Parcelable

@Parcelize
data class StationListData(
    var sid: String? = null,
    var store_id: String? = null,
    var store_type: String? = null,
    var store_name: String? = null,
    var shopping_area: String? = null,
    var store_phone: String? = null,
    var store_address: String? = null,
    var store_website: String? = null,
    var store_facebook: String? = null,
    var store_news: String? = null,
    var store_picture: String? = null,
    var store_latitude: String? = null,
    var store_longitude: String? = null,
    var store_status: String? = null,
    var distance: String? = null
) : Parcelable

@Parcelize
data class BookingInfoData(
    var rid: String? = null,
    var booking_no: String? = null,
    var store_id: String? = null,
    var reserve_date: String? = null,
    var reserve_time: String? = null,
    var mid: String? = null,
    var member_id: String? = null,
    var member_name: String? = null,
    var member_phone: String? = null,

    var service_item: String? = null,
    var service_name: String? = null,
    var service_price: String? = null,
    var reserve_status: String? = null,
    var reserve_created_at: String? = null,
    var message_log: String? = null,
    var message_pic: String? = null,

    var outpatient_room: String? = null,
    var outpatient_date: String? = null,
    var outpatient_time: String? = null,
    var outpatient_no: String? = null,
    var outpatient_record: String? = null,
    var meetingUrl2: String? = null,
    var total_no: String? = null,
    var now_no: String? = null,
) : Parcelable

@Parcelize
data class DivisionListData(
    var did: String? = null,
    var store_id: String? = null,
    var gid: String? = null,
    var group_division: String? = null,
    var division_code: String? = null,
    var division_name: String? = null
) : Parcelable

@Parcelize
data class CheckItemListData(
    var code: String? = null,
    var list: List<ItemListBean> = listOf(),
    var responseMessage: String? = null,
    var status: String? = null
) : Parcelable

@Parcelize
data class AddOrderData(
    var code: String? = null,
    var responseMessage: String? = null,
    var status: String? = null
) : Parcelable

@Parcelize
data class ItemListBean(
    var 內容: String? = null,
    var 日期: String? = null,
    var 狀態: String? = null,
    var 科室: String? = null,
    var 項次: String? = null
) : Parcelable

@Parcelize
data class CheckItemData(
    var code: String? = null,
    var list: String? = null,
    var responseMessage: String? = null,
    var status: String? = null
) : Parcelable

@Parcelize
data class CheckData(
    var code: String? = null,
    var list: List<CheckBean>? = null,
    var responseMessage: String? = null,
    var status: String? = null
) : Parcelable

@Parcelize
data class CheckBean(
    var 我的號碼: String? = null,
    var 檢驗項目: String? = null,
    var 目前進度: String? = null
) : Parcelable

@Parcelize
data class DrugInfoData(
    var code: String? = null,
    var list: List<DrugInfoBean>? = null,
    var responseMessage: String? = null,
    var status: String? = null
) : Parcelable

@Parcelize
data class DrugInfoBean (
    var 用藥項目清單: List<DrugItemList>? = null,
    var 看診日: String? = null,
    var 領藥號: String? = null
) : Parcelable

@Parcelize
data class DrugItemList(
    var 副作用: String? = null,
    var 劑量: String? = null,
    var 外觀: String? = null,
    var 日份: String? = null,
    var 用法: String? = null,
    var 用藥須知: String? = null,
    var 總量: String? = null,
    var 藥品簡稱: String? = null,
    var 適應症: String? = null,
    var 頻率: String? = null
) : Parcelable

@Parcelize
data class PhysicianListData(
    var pid: String? = null,
    var store_id: String? = null,
    var did: String? = null,
    var doctor_name: String? = null,
    var job_title: String? = null,
    var education: String? = null,
    var expertise: String? = null,
    var physician_status: String? = null,
    var division_code: String? = null,
    var division_name: String? = null,
    var physician_picture: String? = null
) : Parcelable


@Parcelize
data class BookingRecordData(
    var rid: String? = null,
    var booking_no: String? = null,
    var store_id: String? = null,
    var hid: String? = null,
    var reserve_date: String? = null,
    var reserve_time: String? = null,
    var mid: String? = null,
    var member_id: String? = null,
    var member_name: String? = null,
    var service_item: String? = null,
    var reserve_status: String? = null,
    var reserve_created_at: String? = null,
    var store_name: String? = null,
    var store_picture: String? = null
) : Parcelable


@Parcelize
data class WorkingDay201Data(
    var workingdate: String? = null,
    var workingtype: String? = null,
    var timeperiod: String? = null,
    var activity: String? = null
) : Parcelable

@Parcelize
open class hbResponse(
    var message_log: String? = null
) : Parcelable

@Parcelize
open class AcInResponse(
    var activity_name: String? = null,
    var store_id: String? = null,
    var store_name: String? = null,
    var service_code: String? = null,
    var service_name: String? = null,
    var activity_date: String? = null,
    var activity_enddate: String? = null,
    var activity_starttime: String? = null,
    var activity_endtime: String? = null,
    var activity_descript: String? = null,
    var activity_picture: String? = null,
    var activity_count: String? = null,
    var activity_status: String? = null,
) : Parcelable


// ==========================================================
@Parcelize
open class BaseBookResponse(
    var status: String? = null,
    var code: String? = null,
) : Parcelable

// 添加關懷清單
@Parcelize
data class AddCarelistData(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

// 取得我關注的
@Parcelize
data class CareListData(
    var responseMessage: List<CareListVO>? = arrayListOf()
) : Parcelable, BaseBookResponse()

@Parcelize
data class CareListVO(
    var cid: String? = null,
    var member_id: String? = null,
    var cmember_id: String? = null,
    var cmember_pwd: String? = null,
    var nick_name: String? = null,
    var status: String? = null,
    var approve_date: String? = null,
    var fmember_id: String? = null,
    var fmember_name: String? = null
) : Parcelable

// 取得關注我的
@Parcelize
data class GetCaredListData(
    var responseMessage: List<CareListVO>? = arrayListOf()
) : Parcelable, BaseBookResponse()

// 更新關懷暱稱
@Parcelize
data class UpdateCareNickName(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

// 刪除關懷對象
@Parcelize
data class DeleteCareMember(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

// 取消被關懷對象
@Parcelize
data class CancelCaredMember(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

// 通知數
@Parcelize
data class MessageBoxCountVO(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

// 通知數
@Parcelize
data class NotifyResponse(
    var responseMessage: String = "",
    var list : List<NotifyHistoryListVO>? = null
) : Parcelable, BaseBookResponse()

// 取得通知清單
@Parcelize
data class NotifyHistoryListVO(
    var rid: String? = null,
    var message_from: String? = null,
    var message_to: String? = null,
    var message_title: String? = null,
    var message_descript: String? = null,
    var message_type: String? = null,
    var status: String? = null,
    var message_date: String? = null
) : Parcelable

// 更新訊息狀態
@Parcelize
data class MessageBoxStatus(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

// 更新訊息狀態
@Parcelize
data class AuthCareStatus(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

@Parcelize
data class BookingDayListData(
    var responseMessage: List<BookingDayData>? = arrayListOf()
) : Parcelable, BaseBookResponse()

@Parcelize
data class BookingDayData(
    var rid: String? = null,
    var booking_no: String? = null,
    var reserve_time: String? = null,
    var count: String? = null
) : Parcelable

// (58)	Tours app查詢視訊諮詢工作日曆與可預約日期
@Parcelize
data class PhysicianWorkingDayListData(
    var responseMessage: List<PhysicianResponseData>? = arrayListOf()
) : Parcelable, BaseBookResponse()

@Parcelize
data class PhysicianResponseData(
    var workingdate: String? = null,
    var workingtype: String? = null,
    var timeperiod: List<PhysicianTimeperiod>? = arrayListOf()
) : Parcelable

@Parcelize
data class PhysicianTimeperiod(
    var starttime: String? = null,
    var endtime: String? = null,
    var price: String? = null,
    var count: String? = null
) : Parcelable

// (59)	Tours app新增視訊諮詢預約單
@Parcelize
data class CommonData(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

@Parcelize
data class VideoRecordListData(
    var rid: String? = null,
    var did: String? = null,
    var pid: String? = null,
    var booking_no: String? = null,
    var store_id: String? = null,
    var reserve_date: String? = null,
    var reserve_time: String? = null,
    var reserve_endtime: String? = null,
    var price: String? = null,
    var mid: String? = null,
    var member_phone: String? = null,
    var member_name: String? = null,
    var member_email: String? = null,
    var question: String? = null,
    var pay_type: String? = null,
    var pay_status: String? = null,
    var reserve_status: String? = null,
    var reserve_created_at: String? = null,
    var store_name: String? = null,
    var store_picture: String? = null,
    var doctor_name: String? = null,
    var division_name: String? = null,
    var physician_picture: String? = null,
    var invoice_type: String? = null,
    var invoice_address: String? = null,
) : Parcelable

// (63)	Tours app查詢視訊諮詢診次資料
@Parcelize
data class VideoRecordOutpatientData(
    var rid: String? = null,
    var sid: String? = null,
    var outpatient_room: String? = null,
    var outpatient_date: String? = null,
    var outpatient_time: String? = null,
    var start_time: String? = null,
    var stop_time: String? = null,
    var booking_no: String? = null,
    var member_name: String? = null,
    var member_phone: String? = null,
    var meetingUrl2: String? = null,
    var outpatient_flag: String? = null,
    var reserve_status: String? = null,
    var store_name: String? = null,
    var total_no: String? = null,
    var now_no: String? = null,
    var memo: String? = null
) : Parcelable

@Parcelize
data class AddBookingData(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

@Parcelize
data class CancelBookingData(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

@Parcelize
data class messagelogData(
    var responseMessage: String = ""
) : Parcelable, BaseBookResponse()

@Parcelize
data class WorkingDay2Data(
    var responseMessage: List<WorkingDay201Data>? = arrayListOf()

) : Parcelable, BaseBookResponse()


@Parcelize
open class ParentHisRecordListData(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null,
    var list : List<HisRecordListData>? = null
) : Parcelable
@Parcelize
data class HisRecordListData(
    var counter: Int, // 摘要編號
    var 日期: String
) : Parcelable

@Parcelize
open class ParentHisRecordInfoData(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null,
    var list : List<HisRecordInfoData>? = null
) : Parcelable
@Parcelize
data class HisRecordInfoData(
    var counter: Int? = null, // 摘要編號
    var 日期: String? = null,
    var 病摘內容: String? = null
) : Parcelable