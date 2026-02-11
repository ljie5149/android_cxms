package com.ptv.ibeacon.receiver.vast

import android.util.Log
import com.ptv.ibeacon.receiver.apiIBeacon.ApiRepositoryIBeacon
import com.ptv.ibeacon.receiver.apiIBeacon.VastResponse
import com.ptv.ibeacon.receiver.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImpressionHandler {
    private val repository = ApiRepositoryIBeacon()
    private val TAG = "ImpressionHandler"

    fun handleTrackingUrls (it: VastResponse, vastModel: VASTModel, uuid: String?) {
        // Collect impressions
        val impressionData = vastModel.getImpression(it)?.let { cdata ->
            vastModel.extractUrlFromCData(cdata)
        }

        Log.i("${TAG}_VAST_CALL", "Collected Impression URL: $impressionData")
//        Logger.addLog("006: Collected Impression URL: $impressionData")
        uuid?.let { it1 -> Logger.addRemoteLog(it1, "Collected Impression URL: $impressionData") }

        // Collect tracking URLs
        val trackingUrls = vastModel.collectTrackingUrls(it)
        trackingUrls.forEach { trackingUrl ->
            Log.i("${TAG}_VAST_CALL", "Collected Tracking URL: $trackingUrl")
//            Logger.addLog("007: Collected Tracking URL: $trackingUrl")
            uuid?.let { it1 -> Logger.addRemoteLog(it1, "Collected Tracking URL: $trackingUrl") }
        }

        // Collect video click
        val clickUrls = vastModel.collectVideoClickUrls(it)
        clickUrls.forEach { clickUrl ->
            Log.i("${TAG}_VAST_CALL", "Collected Video Click URL: $clickUrl")
//            Logger.addLog("008: Collected Video Click URL: $clickUrl")
            uuid?.let { it1 -> Logger.addRemoteLog(it1, "Collected Video Click URL: $clickUrl") }
        }

        Logger.addLog("006: Calling impressionData, trackingUrls (${trackingUrls.size}), and clickUrls (${clickUrls.size})")
        callImpressionTrackingClickUrlsSequentially(impressionData, trackingUrls, clickUrls)
    }

    private suspend fun makeApiCall(url: String) {
        try {
            withContext(Dispatchers.IO) {
                // Make the API call using Retrofit
                val response = repository.makeApiCall(url)
                // Log the response from the API
                Log.i("VAST_CALL_API_CALL", "Successful API call to $url with response: $response")
            }
        } catch (e: Exception) {
            Log.e("VAST_CALL_API_CALL", "Failed API call to $url", e)
        }
    }

    private val customScope = CoroutineScope(Dispatchers.Main)

    private fun callImpressionTrackingClickUrlsSequentially(
        impressionData: String?,
        trackingUrls: List<String>?,
        videoClickUrls: List<String>?
    ) {
        // Start a coroutine scope
        customScope.launch {
            // Call impression URL if it exists
            var count = 0;

            impressionData?.let {
                count += 1
                Logger.addLog("006-${count}: Calling impression URL: $it")
                makeApiCall(it)
            }

            // Call each tracking URL sequentially
            trackingUrls?.forEach { trackingUrl ->
                trackingUrl?.let {
                    count += 1
                    Logger.addLog("006-${count}: Calling tracking URL: $it")
                    makeApiCall(it)
                }
            }

            // Call each video click URL sequentially
            videoClickUrls?.forEach { clickUrl ->
                clickUrl?.let {
                    count += 1
                    Logger.addLog("006-${count}: Calling videoClick URL: $it")
                    makeApiCall(it)
                }
            }
        }
    }
}