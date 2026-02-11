package com.ptv.ibeacon.receiver.apiIBeacon

import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiServiceIBeacon {
    @GET("getVast")
    suspend fun getVastResponse(
        @Query("screenid") screenId: String,
        @Query("mode") mode: String,
        @Query("uuid") uuid: String,
        @Query("deviceid") deviceId: String
    ): Response<VastResponse>

    @GET
    suspend fun makeApiCall(@Url url: String): String
}