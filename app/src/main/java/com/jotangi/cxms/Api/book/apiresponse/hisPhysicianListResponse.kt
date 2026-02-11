package com.jotangi.cxms.Api.book.apiresponse


import com.google.gson.annotations.SerializedName

class hisPhysicianListResponse : ArrayList<hisPhysicianListResponse.hisPhysicianListResponseItem>(){
    data class hisPhysicianListResponseItem(
        @SerializedName("代班醫師代號")
        val 代班醫師代號: String?,
        @SerializedName("代班醫師名")
        val 代班醫師名: String?,
        @SerializedName("備註")
        val 備註: String?,
        @SerializedName("可掛號否")
        val 可掛號否: String?,
        @SerializedName("就診參考序號")
        val 就診參考序號: String?,
        @SerializedName("已掛號人數")
        val 已掛號人數: String?,
        @SerializedName("排班識別碼")
        val 排班識別碼: String?,
        @SerializedName("日期")
        val 日期: String?,
        @SerializedName("樓層代碼")
        val 樓層代碼: String?,
        @SerializedName("班別")
        val 班別: String?,
        @SerializedName("班別代碼")
        val 班別代碼: String?,
        @SerializedName("科別")
        val 科別: String?,
        @SerializedName("診別")
        val 診別: String?,
        @SerializedName("診別代碼")
        val 診別代碼: String?,
        @SerializedName("醫師代號")
        val 醫師代號: String?,
        @SerializedName("醫師名")
        val 醫師名: String?,
        @SerializedName("限數")
        val 限數: Int?,
        @SerializedName("院所代號")
        val 院所代號: String?
    )
}