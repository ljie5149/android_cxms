package com.ptv.ibeacon.receiver.vast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.ptv.ibeacon.receiver.apiIBeacon.ApiRepositoryIBeacon
import com.ptv.ibeacon.receiver.apiIBeacon.MediaFile
import com.ptv.ibeacon.receiver.apiIBeacon.VastResponse

class VASTModel : ViewModel() {
    private val repository = ApiRepositoryIBeacon()
    private val TAG = "VAST_CALL_VASTModel"
    private val _apiResponse = MutableLiveData<VastResponse?>()

    val apiResponse: LiveData<VastResponse?> = _apiResponse

    fun getApiData(uuid:String, deviceId: String) {
        viewModelScope.launch {
            val response = repository.fetchVastResponse(uuid, deviceId)
            Log.i(TAG, response.toString())
            _apiResponse.postValue(response)
        }
    }

    fun getFirstMediaFile(vastResponse: VastResponse?): MediaFile? {
        // Navigate through the response to find the first MediaFile
        if (vastResponse == null) return null;
        return vastResponse?.ad?.inLine?.creatives?.creative?.mediaFiles?.mediaFileList?.firstOrNull()
    }

    fun getImpression(vastResponse: VastResponse?): String? {
        if (vastResponse == null) return null;
        return vastResponse?.ad?.inLine?.impression
    }

    fun collectTrackingUrls(vastResponse: VastResponse?): List<String> {
        val trackingUrls = mutableListOf<String>()
        vastResponse?.ad?.inLine?.creatives?.creative?.trackingEvents?.trackingList?.forEach { tracking ->
            val trackingUrl = tracking.trackingUrl
            trackingUrl?.let {
                extractUrlFromCData(it)?.let { it1 -> trackingUrls.add(it1) }
            }
        }

        return trackingUrls
    }

    fun collectVideoClickUrls(vastResponse: VastResponse?): List<String> {
        val trackingUrls = mutableListOf<String>()
        vastResponse?.ad?.inLine?.creatives?.creative?.videoClicks?.clickTrackingList?.forEach { tracking ->
            tracking?.let {
                extractUrlFromCData(it)?.let { it1 -> trackingUrls.add(it1) }
            }
        }

        return trackingUrls
    }

    fun getDuration(vastResponse: VastResponse?): String? {
        if (vastResponse == null) return null;
        return vastResponse?.ad?.inLine?.creatives?.creative?.duration ?: null
    }

    fun extractUrlFromCData(cdata: String?): String? {
        if (cdata == null) return cdata
        // Check if the string starts with <![CDATA[ and ends with ]]>
        return if (cdata.startsWith("<![CDATA[") && cdata.endsWith("]]>")) {
            // Remove <![CDATA[ and ]]>
            cdata.removePrefix("<![CDATA[").removeSuffix("]]>")
        } else {
            // Return the original string if it's not in CDATA format
            cdata
        }
    }
}