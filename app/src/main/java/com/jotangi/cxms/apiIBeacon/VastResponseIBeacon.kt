package com.ptv.ibeacon.receiver.apiIBeacon
import org.simpleframework.xml.*

@Root(name = "VAST", strict = false)
data class VastResponse(
    @field:Attribute(name = "version", required = false)
    var version: String? = null,

    @field:Element(name = "Ad", required = false)
    var ad: Ad? = null
)

@Root(name = "Ad", strict = false)
data class Ad(
    @field:Attribute(name = "id", required = false)
    var id: String? = null,

    @field:Element(name = "InLine", required = false)
    var inLine: InLine? = null
)

@Root(name = "InLine", strict = false)
data class InLine(
    @field:Element(name = "AdSystem", required = false)
    var adSystem: String? = null,

    @field:Element(name = "AdTitle", required = false)
    var adTitle: String? = null,

    @field:Element(name = "Impression", required = false)
    var impression: String? = null,

    @field:Element(name = "Creatives", required = false)
    var creatives: Creatives? = null
)

@Root(name = "Creatives", strict = false)
data class Creatives(
    @field:Element(name = "Creative", required = false)
    var creative: Creative? = null
)

@Root(name = "Creative", strict = false)
data class Creative(
    @field:Attribute(name = "id", required = false)
    var id: String? = null,

    @field:Element(name = "Duration", required = false)
    var duration: String? = null,

    @field:Element(name = "TrackingEvents", required = false)
    var trackingEvents: TrackingEvents? = null,

    @field:Element(name = "VideoClicks", required = false)
    var videoClicks: VideoClicks? = null,

    @field:Element(name = "MediaFiles", required = false)
    var mediaFiles: MediaFiles? = null
)

@Root(name = "TrackingEvents", strict = false)
data class TrackingEvents(
    @field:ElementList(entry = "Tracking", inline = true, required = false)
    var trackingList: List<Tracking>? = null
)

@Root(name = "Tracking", strict = false)
data class Tracking(
    @field:Attribute(name = "event", required = false)
    var event: String? = null,

    @field:Text(required = false)
    var trackingUrl: String? = null
)

@Root(name = "VideoClicks", strict = false)
data class VideoClicks(
    @field:ElementList(entry = "ClickTracking", inline = true, required = false)
    var clickTrackingList: List<String>? = null
)

@Root(name = "MediaFiles", strict = false)
data class MediaFiles(
    @field:ElementList(entry = "MediaFile", inline = true, required = false)
    var mediaFileList: List<MediaFile>? = null
)

@Root(name = "MediaFile", strict = false)
data class MediaFile(
    @field:Attribute(name = "delivery", required = false)
    var delivery: String? = null,

    @field:Attribute(name = "type", required = false)
    var type: String? = null,

    @field:Attribute(name = "bitrate", required = false)
    var bitrate: Int? = null,

    @field:Attribute(name = "width", required = false)
    var width: Int? = null,

    @field:Attribute(name = "height", required = false)
    var height: Int? = null,

    @field:Attribute(name = "duration", required = false)
    var duration: Int? = null,

    @field:Attribute(name = "linkto", required = false)
    var linkto: String? = null,

    @field:Text(required = false)
    var url: String? = null
)
