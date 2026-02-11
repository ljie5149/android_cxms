package com.ptv.ibeacon.receiver.apiIBeacon

import android.util.Log
import com.jotangi.cxms.CONSTANT
import com.ptv.ibeacon.receiver.logger.Logger

class ApiRepositoryIBeacon {
    private val api = RetrofitInstanceIBeacon.api
    private val TAG = "VAST_CALL_ApiRepository"

    suspend fun fetchVastResponse(uuid: String, deviceId: String): VastResponse? {
        return try {
            var logMessage = "004: Calling VAST API: ${CONSTANT.BASE_URL}?screenid=${CONSTANT.SCREEN_ID}&mode=${CONSTANT.MODE}&uuid=${uuid}&deviceid=${deviceId}"
            Logger.addLog(logMessage)
            Logger.addRemoteLog(uuid, logMessage)
            val response = api.getVastResponse(
                screenId = CONSTANT.SCREEN_ID,
                mode = CONSTANT.MODE,
                uuid,
                deviceId
            )
            Log.i(TAG, response.toString())

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }

    suspend fun makeApiCall(url: String): Unit? {
        return try {
            api.makeApiCall(url)
            null
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }
}