package com.ptv.ibeacon.receiver.vast

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide

object Banner {
    // default value
    private var defaultBannerLinkTo = "https://www.pilottv.com.tw"
    private var defaultBanner = "default_banner.jpg"

    fun displayBanner(context: Context, playerView: PlayerView, clickableBannerView: ImageView, url: String?, linkTo: String?) {
        // prepare view
        playerView.visibility = View.GONE
        clickableBannerView.visibility = View.VISIBLE

        try {
            if (url != null) {
                Glide.with(context)
                    .load(url)
                    .into(clickableBannerView)
            } else {
                // Load the default image from assets
                val inputStream = context.assets.open("default_banner.jpg")

                // Decode the input stream into a bitmap
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Set the bitmap to the ImageView
                clickableBannerView.setImageBitmap(bitmap)

                // Close the input stream
                inputStream.close()
            }

            // listen on click
            clickableBannerView.setOnClickListener {
                if (linkTo != null) {
                    openNewTabInBrowser(linkTo, context)
                } else {
                    openNewTabInBrowser(defaultBannerLinkTo, context)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Handle the exception, fallback to default if needed

            // In case of an exception, load the default image from assets
            try {
                val inputStream = context.assets.open(defaultBanner)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                clickableBannerView.setImageBitmap(bitmap)
                inputStream.close()
            } catch (innerException: Exception) {
                innerException.printStackTrace() // Handle inner exception
            }
        }
    }

    private fun openNewTabInBrowser(url: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}