package com.ptv.ibeacon.receiver.vast

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

object VideoPlayer {
    private var defaultVideo = "asset:///default_video.mp4"

    fun play(clickableBannerView: ImageView, playerView: PlayerView, player: ExoPlayer, url: String?) {
        // in case player still playing, do nothing
        if (player.isPlaying) {
            return;
        }

        // prepare view
        clickableBannerView.visibility = View.GONE
        playerView.visibility = View.VISIBLE

        // Check if the URL is valid and not empty
        val mediaItem = if (!url.isNullOrEmpty()) {
            // Use the provided URL
            val uri = Uri.parse(url)
            MediaItem.fromUri(uri)
        } else {
            // Fallback to default video from assets
            val defaultUri = Uri.parse(defaultVideo)
            MediaItem.fromUri(defaultUri)
        }

        // mute
        player.volume = 0f

        // Set the media item
        player.setMediaItem(mediaItem)
        player.prepare()

        // Bind the player to the PlayerView
        player.playWhenReady = true
    }
}