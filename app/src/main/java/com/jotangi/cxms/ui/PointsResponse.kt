package com.jotangi.cxms.ui

import com.google.gson.annotations.SerializedName

data class PointsResponse(
    val status: String,
    val code: String,
    val data: List<PointData>
)

data class PointData(
    @SerializedName("0")
    val point0: String,

    @SerializedName("total_point")
    val totalPoint: String
)
