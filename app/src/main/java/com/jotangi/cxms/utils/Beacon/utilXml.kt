package com.jotangi.NumberHealthy.utils.Beacon

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

data class VastAd(
    val id: String,
    val adSystem: String,
    val adTitle: String,
    val impression: String,
    val creatives: List<Creative>
)

data class Creative(
    val id: String,
    val duration: String,
    val trackingEvents: Map<String, String>,
    val clickThrough: String,
    val clickTrackings: List<String>,
    val mediaFiles: List<MediaFile>
)

data class MediaFile(
    val delivery: String,
    val type: String,
    val bitrate: Int,
    val width: Int,
    val height: Int,
    val url: String
)
class utilXml {
    var html_str: String =""
    fun fetchHtml(url: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val doc = Jsoup.connect(url).get()
            // Process the document here
            html_str = doc.toString()
        }
    }
    fun parse(xmlData: String) {
        val doc = Jsoup.parse(xmlData, "", org.jsoup.parser.Parser.xmlParser())

        val name = doc.select("MediaFiles").text()
        val age = doc.select("from").text()
        parseVastXml(xmlData)
        println("david AdTitle: $name")
        println("david Age: $age")
    }
    fun parseVastXml(xml: String): kotlin.collections.MutableList<MediaFile>? {
        var vastAd: VastAd? = null
        var currentCreative: Creative? = null
        var currentMediaFile: MediaFile? = null
        var currentTrackingEvent: Pair<String, String>? = null
        var text: String? = null

        val creatives = mutableListOf<Creative>()
        val trackingEvents = mutableMapOf<String, String>()
        val clickTrackings = mutableListOf<String>()
        val mediaFiles = mutableListOf<MediaFile>()

        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "Ad" -> {
                            val adId = parser.getAttributeValue(null, "id")
                            vastAd = VastAd(adId, "", "", "", listOf())
                        }
                        "Creative" -> {
                            val creativeId = parser.getAttributeValue(null, "id")
                            currentCreative = Creative(creativeId, "", trackingEvents, "", clickTrackings, mediaFiles)
                        }
                        "Tracking" -> {
                            val event = parser.getAttributeValue(null, "event")
                            currentTrackingEvent = event to ""
                        }
                    }
                }
                XmlPullParser.TEXT -> text = parser.text
                XmlPullParser.END_TAG -> {
                    when (tagName) {
                        "AdSystem" -> vastAd = vastAd?.copy(adSystem = text ?: "")
                        "AdTitle" -> vastAd = vastAd?.copy(adTitle = text ?: "")
                        "Impression" -> vastAd = vastAd?.copy(impression = text ?: "")
                        "Duration" -> currentCreative = currentCreative?.copy(duration = text ?: "")
                        "Tracking" -> currentTrackingEvent = currentTrackingEvent?.copy(second = text ?: "")
                            ?.also { trackingEvents[it.first] = it.second }
                        "ClickThrough" -> currentCreative = currentCreative?.copy(clickThrough = text ?: "")
                        "ClickTracking" -> clickTrackings.add(text ?: "")
                        "MediaFile" -> {
                            val delivery = parser.getAttributeValue(null, "delivery") ?: ""
                            val type = parser.getAttributeValue(null, "type") ?: ""
                            val bitrate = parser.getAttributeValue(null, "bitrate")?.toInt() ?: 0
                            val width = parser.getAttributeValue(null, "width")?.toInt() ?: 0
                            val height = parser.getAttributeValue(null, "height")?.toInt() ?: 0
                            var url =text!!.replace("\t", "")
                            url =url.replace("\n", "")
                            currentMediaFile = MediaFile(delivery, type, bitrate, width, height, url ?: "")
                            mediaFiles.add(currentMediaFile)
                        }
                        "Creative" -> {
                            currentCreative?.let { creatives.add(it) }
                            currentCreative = null
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        vastAd = vastAd?.copy(creatives = creatives)
        return mediaFiles
    }

}