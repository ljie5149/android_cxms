package com.ptv.ibeacon.receiver.vast

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ptv.ibeacon.receiver.logger.Logger

class VASTDataHandler(
    private val context: Context,
    private val clickableBannerView: ImageView,
    private val playerView: PlayerView,
    private val player: ExoPlayer,
    private val deviceId: String,
    private val vastModel: VASTModel,
) {
    private var uuid: String? = null
    private var major: Int? = null
    private var minor: Int? = null
    private var rssi: Int? = null
    private var txPower: Int? = null

    private var TAG = "VASTDataHandler"
    private var lastBannerDisplayTimestamp: Long = 0
    private var bannerDisplayDuration: Long = 0


    fun registerObserver(lifecycleOwner: LifecycleOwner) {
        vastModel.apiResponse.observe(lifecycleOwner) { response ->
            val currentTime = System.currentTimeMillis()
            if (response?.ad != null) {
                val firstMediaFile = vastModel.getFirstMediaFile(response)
                //  Extract the URL from the CDATA
                val mediaUrl = firstMediaFile?.url?.let { cdata ->
                    vastModel.extractUrlFromCData(cdata)
                }

                var duration = vastModel.getDuration(response);
                Log.i("VAST_RESPONSE", "files: $firstMediaFile")

                if (mediaUrl != null) {
                    when {
                        (firstMediaFile?.type?.contains("video", ignoreCase = true) == true) -> {
                            // VIDEO
                            VideoPlayer.play(clickableBannerView, playerView, player, mediaUrl)
                            bannerDisplayDuration = 0

                            var logMessage = "005: Extracted Video URL: $mediaUrl, duration: ${duration}, linkto: ${firstMediaFile.linkto}"
                            Log.i("VAST_RESPONSE", logMessage)
                            Logger.addLog(logMessage)
                            uuid?.let { Logger.addRemoteLog( it, logMessage) }
                        }
                        (firstMediaFile?.type?.contains("image", ignoreCase = true) == true)-> {
                            // IMAGE
                            var bannerDuration = timeToMillis(duration, 10000) // 10000 milliseconds = 10 seconds

                            lastBannerDisplayTimestamp = currentTime
                            bannerDisplayDuration = bannerDuration
                            Banner.displayBanner(context, playerView, clickableBannerView, mediaUrl, firstMediaFile.linkto)

                            var logMessage = "005: Extracted image URL: $mediaUrl, duration: ${duration}, linkto: ${firstMediaFile.linkto}"
                            Log.i("VAST_RESPONSE", logMessage)
                            Logger.addLog(logMessage)
                            uuid?.let { Logger.addRemoteLog( it, logMessage) }
                        }
                        else -> {
                            bannerDisplayDuration = 0
                            var logMessage = "005: Media URL does not contain 'video' or 'image'. No action taken."
                            Log.i("VAST_RESPONSE", logMessage)
                            Logger.addLog(logMessage)
                            uuid?.let { Logger.addRemoteLog( it, logMessage) }
                        }
                    }
                }

                // handle tracking urls
                ImpressionHandler.handleTrackingUrls(response, vastModel, uuid)
            } else {
                Log.i("VAST_RESPONSE", "Response is null")
                Logger.addLog("005: Ad is null, preventing further API calls for 1 hour.")
                uuid?.let { Logger.addRemoteLog( it, "Ad is null, preventing further API calls for 1 hour.") }

                // If the Ad is null, mark the current time and prevent further API calls for 1 hour
                val prefs = context.getSharedPreferences("VastPrefs", Context.MODE_PRIVATE)

                prefs.edit().putLong("lastAdCheckTimestamp", currentTime).apply()
                Log.i("${TAG}_VAST_CALL", "Ad is null, preventing further API calls for 1 hour.")
            }
        }
    }

    fun handleIBeaconData(_uuid: String?, _major: Int?, _minor: Int?, _rssi: Int?, _txPower: Int?) {
        // assign data for further use
        uuid = _uuid
        major = _major
        minor = _minor
        rssi = _rssi
        txPower = _txPower

        Log.i("${TAG}_VAST_CALL_TEST", "UUID received, should only called one")

        _uuid?.let {
            processUUID(_uuid)
            Logger.addRemoteLog(_uuid, "Beacon Received")
        }
    }

    fun processUUID(uuid: String) {
        val prefs = context.getSharedPreferences("VastPrefs", Context.MODE_PRIVATE)
        val lastAdCheckTimestamp = prefs.getLong("lastAdCheckTimestamp", 0L)

        // Check if 1 hour (3600000 milliseconds) has passed since the last null Ad response
//        val currentTime = System.currentTimeMillis()
//        if (currentTime - lastAdCheckTimestamp < 3600000) {
//            Logger.addLog("003: IBeacon data received, UUID = ${uuid} but skipping API call. Last null Ad response was within 1 hour.")
//            Logger.addRemoteLog(
//                uuid,
//                "IBeacon data received, but skipping VAST api call. Last null Ad response was within 1 hour."
//            )
//            Log.i("${TAG}_VAST_CALL", "Skipping API call. Last null Ad response was within 1 hour.")
//            return
//        }

        // Check if the player is currently playing a video
        if (player.isPlaying) {
            Logger.addLog("003: IBeacon data received, UUID = ${uuid} but video still playing. Skipping VAST api call")
            Logger.addRemoteLog(uuid, "IBeacon data while video still playing")

            Log.i("${TAG}_VAST_CALL", "Video is already playing. Skipping playVideo call.")
            return
        }

        // Check if x seconds have passed since the last banner was displayed
//        if (bannerDisplayDuration.toInt() != 0 && currentTime - lastBannerDisplayTimestamp < bannerDisplayDuration) {
//            Logger.addLog("003: IBeacon data received, UUID = ${uuid} but skipping API call. Last banner displayed was within ${bannerDisplayDuration} miliseconds.")
//            Logger.addRemoteLog(uuid, "IBeacon data received, but skipping VAST API call. Last banner displayed was within ${bannerDisplayDuration} miliseconds.")
//            Log.i("${TAG}_VAST_CALL", "Skipping API call. Last banner displayed was within ${bannerDisplayDuration} miliseconds.")
//            return
//        }

        Logger.addLog("003: IBeacon data received, UUID = ${uuid}")
        Banner.displayBanner(context, playerView, clickableBannerView, null, null)

        vastModel.getApiData(uuid, deviceId)
    }

    private fun timeToMillis(time: String?, defaultValue: Long): Long {
        if (time == null) return defaultValue
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()

        return (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L)
    }
}