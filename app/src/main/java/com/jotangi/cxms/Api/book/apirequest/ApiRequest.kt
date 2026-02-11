package com.jotangi.cxms.Api.book.apirequest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * (1)	Tours app會員註冊
 */
@Parcelize
data class UserRegister(
    var auth_token: String ="",
    var memberId: String = "",
    var memberPwd: String = "",
    var memberName: String = "",
    var memberPid: String = "",
    var birthday: String = ""
) : Parcelable


/**
 * (2)	Tours app會員登入
 */
@Parcelize
data class UserLoginRequest(
    var auth_token: String ="",
    var memberPid: String = "",
    var memberPwd: String = "",
    var fcmToken: String?,
    var unique_id: String?
) : Parcelable

/**
 * (3)	Tours app會員忘記密碼
 */

@Parcelize
data class UserResetPwd(
    var auth_token: String ="",
    var memberPid: String = "",
    var memberPwd: String = "",
    var code: String = "",
) : Parcelable


/**
 * (４)	Tours app會員編輯
 */

@Parcelize
data class UserEditRequest(
    var auth_token: String ="",
    var name: String = "",
    var gender: String = "",
    var email: String = "",
    var birthday: String = "",
    var address: String = "",
    var phone: String?,
    var member_id: String,
) : Parcelable

/**
 * (12)	Tours app 查詢HIS系統的醫院科別醫師資料
 */
@Parcelize
data class HisPhysicianListRequest(
    var auth_token: String ="",
    var division: String = "",
    var start: String = "",
    var end: String = "",
) : Parcelable

@Parcelize
data class StartEndData(
    var auth_token: String ="",
    var startList: MutableList<String> = mutableListOf(),
    var endList: MutableList<String> = mutableListOf()
) : Parcelable

/**
 * (13)	HIS系統的一般網路掛號
 */
@Parcelize
data class HisRegistrationRequest(
    var auth_token: String ="",
    var regDate: String = "",
    var scheduleId: String = "",
) : Parcelable

/**
 * (14)
 */
@Parcelize
data class recordDataRequest(
    var auth_token: String ="",
    var id: String = "",
    var pwd: String = "",
    var startdate: String = "",
    var enddate: String = "",
) : Parcelable