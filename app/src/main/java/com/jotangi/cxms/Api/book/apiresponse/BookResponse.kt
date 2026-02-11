package com.jotangi.cxms.Api.book.apiresponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
open class OpenBookResponse(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null,
) : Parcelable

/**
 * 綜合掛號清單
 */
@Parcelize
data class ComplexRegisterListData(
    var type: Int = -1,
    var ymd: String = "",
    var register: HisRegistrationListBean? = null,
    var sleepWell: SleepWellBookingListBean? = null,
) : Parcelable

/**
 * (25)	查詢舒眠館預約紀錄
 */
@Parcelize
data class SleepWellBookingListData(
    @SerializedName("list")
    var list: List<SleepWellBookingListBean> = listOf()
) : Parcelable, OpenBookResponse()

@Parcelize
data class SleepWellBookingListBean(
    @SerializedName("booking_no")
    var bookingNo: String? = null,
    @SerializedName("member_id")
    var memberId: String? = null,
    @SerializedName("member_name")
    var memberName: String? = null,
    @SerializedName("member_pid")
    var memberPid: String? = null,
    @SerializedName("mid")
    var mid: String? = null,
    @SerializedName("reserve_date")
    var reserveDate: String? = null,
    @SerializedName("reserve_endtime")
    var reserveEndtime: String? = null,
    @SerializedName("reserve_starttime")
    var reserveStarttime: String? = null,
    @SerializedName("reserve_status")
    var reserveStatus: String? = null,
    @SerializedName("room_no")
    var roomNo: String? = null,
    @SerializedName("shift_code")
    var shiftCode: String? = null,
    @SerializedName("store_id")
    var storeId: String? = null
) : Parcelable

/**
 * (24)	新增舒眠館預約紀錄
 */
@Parcelize
data class AddSleepWellData(
    @SerializedName("list")
    var list: String? = null
) : Parcelable, OpenBookResponse()

/**
 * (23)	查詢舒眠館可預約時間
 */
@Parcelize
data class SleepWellWorkingDayData(
    @SerializedName("list")
    var list: List<SleepWellWorkingDayBean> = listOf(),
) : Parcelable, OpenBookResponse()

@Parcelize
data class SleepWellWorkingDayBean(
    @SerializedName("bookingcount")
    var bookingcount: String? = null,
    @SerializedName("count")
    var count: String? = null,
    @SerializedName("endtime")
    var endtime: String? = null,
    @SerializedName("shiftcode")
    var shiftcode: String? = null,
    @SerializedName("roomno")
    var roomno: String? = null,
    @SerializedName("starttime")
    var starttime: String? = null,
    @SerializedName("workingdate")
    var workingdate: String? = null,
    @SerializedName("workingtype")
    var workingtype: String? = null
) : Parcelable


@Parcelize
data class BannerList2Data(
    @SerializedName("list")
    var list: List<BannerList2Bean> = listOf()
) : Parcelable, OpenBookResponse()

@Parcelize
data class BannerList2Bean(
    @SerializedName("banner_date")
    var bannerDate: String? = null,
    @SerializedName("banner_descript")
    var bannerDescript: String? = null,
    @SerializedName("banner_enddate")
    var bannerEnddate: String? = null,
    @SerializedName("banner_picture")
    var bannerPicture: String? = null,
    @SerializedName("banner_subject")
    var bannerSubject: String? = null,
    @SerializedName("bid")
    var bid: String? = null,
) : Parcelable

@Parcelize
data class BookDataResponse(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null
) : Parcelable

@Parcelize
data class GetUidpwd2Response(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null,
    var member_pid: String? = null,
    var member_pwd: String? = null
) : Parcelable

// (1)	Tours app會員資料查詢
@Parcelize
data class MemberInfoData(
    var mid: String? = null,
    var member_id: String? = null,
    var member_pwd: String? = null,
    var member_name: String? = null,
    var member_type: String? = null,
    var member_gender: String? = null,
    var member_email: String? = null,
    var member_birthday: String? = null,
    var member_address: String? = null,
    var member_phone: String? = null,
    var member_picture: String? = null,
    var member_totalpoints: String? = null,
    var member_usingpoints: String? = null,
    var member_status: String? = null,
    var recommend_code: String? = null,
    var member_sid: String? = null,
    var member_pid: String = "",
) : Parcelable


@Parcelize
data class GetCouponDataBeen(
    var cid: String? = null,
    var coupon_id: String? = null,
    var coupon_type: String? = null,
    var coupon_name: String? = null,
    var coupon_description: String? = null,
    var coupon_startdate: String? = null,
    var coupon_enddate: String? = null,
    var coupon_status: String? = null,
    var coupon_rule: String? = null,
    var coupon_discount: String? = null,
    var discount_amount: String? = null,
    var coupon_storeid: String? = null,
    var coupon_for: String? = null,
    var coupon_picture: String? = null,
    var coupon_point: String? = null,
    var coupon_count: String? = null
) : Parcelable


@Parcelize
data class GetCouponPointBeen(
    var status: String? = null,
    var code: String? = null,
    var responseMessage: String? = null,
    var Points: String? = null,
) : Parcelable


@Parcelize
data class GetStoreApplyListBeen(
    var rid: String? = null,
    var store_id: String? = null,
    var member_id: String? = null,
    var apply_date: String? = null,
    var coupon_no: String? = null,
    var coupon_name: String? = null,
    var coupon_point: String? = null,
    var applycoupon_status: String? = null,
    var applycoupon_created_at: String? = null,
    var applycoupon_created_by: String? = null,
    var applycoupon_trash: String? = null,
    var customer_id: String? = null,

    ) : Parcelable

/**
 * (12)	Tours app 查詢HIS系統的醫院科別醫師資料
 */
@Parcelize
data class PhysicianScheduleData(
    var 院所代號: String? = null,
    var 日期: String? = null,
    var 科別: String? = null,
    var 診別: String? = null,
    var 診別代碼: String? = null,
    var 樓層代碼: String? = null,
    var 班別代碼: String? = null,
    var 班別: String? = null,
    var 醫師名: String? = null,
    var 限數: String? = null,
    var 代班醫師名: String? = null,
    var 排班識別碼: String? = null,
    var 就診參考序號: String? = null,
    var 已掛號人數: String? = null,
    var 備註: String? = null,
    var 醫師代號: String? = null,
    var 代班醫師代號: String? = null,
    var 可掛號否: String? = null,
) : Parcelable

/**
 * (13)	HIS系統的一般網路掛號
 */
@Parcelize
data class HisRegistrationData(
    var list: List<HisRegistrationBean> = arrayListOf()
) : Parcelable, OpenBookResponse()

@Parcelize
data class HisRegistrationBean(
    var 預掛識別碼: String? = null,
    var 掛號序號: String? = null,
    var 院所代號: String? = null,
    var iD: String? = null,
    var bDATE: String? = null,
    var 就醫類別: String? = null,
    var 掛號日期: String? = null,
    var 科別: String? = null,
    var 診別: String? = null,
    var 班別: String? = null,
    var 醫師: String? = null,
    var 排班識別碼: String? = null,
) : Parcelable

/**
 * (14)	Tours app 查詢HIS系統的掛號資料
 */
@Parcelize
data class HisRegistrationListData(
    var list: List<HisRegistrationListBean> = arrayListOf(),
) : Parcelable, OpenBookResponse()

@Parcelize
data class HisRegistrationListBean(
    var 院所代號: String? = null,
    var 日期: String? = null,
    var 科別: String? = null,
    var 診別: String? = null,
    var 班別: String? = null,
    var 就醫類別: String? = null,
    var 醫師名: String? = null,
    var 掛號序號: String? = null,
    var 預掛識別碼: String? = null,
    var 掛號來源: String? = null,
    var 目前狀態: String? = null,
    var 是否可退掛: String? = null,
    var 不可退掛原因: String? = null,
    var 當班否: String? = null
) : Parcelable

/**
 * (24)	Tours app 查詢HIS系統的醫院排班的醫師資料
 */
@Parcelize
data class DivisionDoctorData(
    @SerializedName("科別")
    var 科別: String? = null,
    @SerializedName("醫師名")
    var 醫師名: String? = null
) : Parcelable

@Parcelize
data class HisOplistData(
    var code: String? = null,
    var list: List<HisOplistBean>? = null,
    var responseMessage: String? = null,
    var status: String? = null
) : Parcelable

@Parcelize
data class HisOplistBean(
    var division_code: String? = null,
    var divisionname: String? = null,
    var doctorname: String? = null,
    var hid: String? = null,
    var nextno: Int? = null,
    var nowno: Int? = null,
    var nursename: String? = null,
    var rdate: String? = null,
    var roomcode: String? = null,
    var roomname: String? = null,
    var shiftname: String? = null
) : Parcelable