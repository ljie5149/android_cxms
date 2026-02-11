package com.jotangi.cxms.ui.Member

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentPhysicianMeetingBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtils
import java.util.*

class PhysicianMeetingFragment : BaseFragment() {

    private lateinit var binding: FragmentPhysicianMeetingBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<PhysicianMeetingFragmentArgs>()

    private var isFirstLoading = true
    private var isClose = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhysicianMeetingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFull()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

//        checkPermission()
//        CheckPermision(requireActivity())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && isFirstLoading) {
            isFirstLoading = false

            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            nav.visibility = View.GONE
            setupWeb(args.url)

        } else {

            requireActivity().window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            nav.visibility = View.VISIBLE
            requireActivity().onBackPressed()
        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAPTURE_AUDIO_OUTPUT),
                200
            )
        }
    }

    private fun CheckPermision(activity: Activity) {
        val list = Arrays.asList(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        if (!hasPermission(activity, list)) {

            DialogUtils.showSingle(requireActivity(), "無權限", "請開啟 相機、麥克風 等權限後繼續") {
                startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
            }
            return
        }
    }

    private fun hasPermission(context: Context?, permissions: List<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context, permission
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    run { return false }
                }
            }
        }
        return true
    }

    private fun setupWeb(url: String) {

        binding.webView.run {

            with(settings) {
                javaScriptEnabled = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true

                javaScriptCanOpenWindowsAutomatically = true
                domStorageEnabled = true
                builtInZoomControls = true
                allowFileAccess = true
                setSupportZoom(false)

                saveFormData = true
                cacheMode = WebSettings.LOAD_DEFAULT
                pluginState = WebSettings.PluginState.ON
//                allowContentAccess = true

//                webViewClient = WebViewClient()
//                setAppCacheEnabled(true)
//                loadWithOverviewMode = true
                mediaPlaybackRequiresUserGesture = false
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)

                    Log.d(TAG, "進度: $newProgress")
                    Log.d(TAG, "url: ${binding.webView.url}")
                    if (binding.webView.url == ApiConstant.METTING_URL && isClose) {
                        isClose = false
                        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    request?.grant(request.resources)
                }

                override fun onGeolocationPermissionsShowPrompt(
                    origin: String?,
                    callback: GeolocationPermissions.Callback?
                ) {
                    super.onGeolocationPermissionsShowPrompt(origin, callback)
                    callback!!.invoke(origin, true, false)
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view!!.loadUrl(url)
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    Log.w(TAG, "url: $url")
                }
            }

            loadUrl(url)
        }
    }
}