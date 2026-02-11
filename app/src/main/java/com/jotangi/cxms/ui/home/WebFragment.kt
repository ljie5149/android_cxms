package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentWebBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.AESCrypt.aesEncrypt
import com.jotangi.cxms.utils.SharedPreferencesUtil
import java.net.URLEncoder


class WebFragment : BaseFragment() {

    private lateinit var binding: FragmentWebBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<WebFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleName = "趨勢圖"
        val id = SharedPreferencesUtil.instances.getAccountId()
        val mid = SharedPreferencesUtil.instances.getAccountMid()
        var url = ""
        Log.d(TAG, "args.title: ${args.title}")

        when (args.title) {

            getString(R.string.circle_heart_rate) -> {
                setToolbarArrow("${getString(R.string.circle_heart_rate)}$titleName")
                url = ApiConstant.HHQ_URL + mid
            }
            getString(R.string.circle_blood_pressure) -> {
                setToolbarArrow("${getString(R.string.circle_blood_pressure)}$titleName")
                url = ApiConstant.HBQ_URL + mid
            }
            getString(R.string.circle_blood_oxygen) -> {
                setToolbarArrow("${getString(R.string.circle_blood_oxygen)}$titleName")
                url = ApiConstant.HOQ_URL + mid
            }
            getString(R.string.circle_breath_rate) -> {
                setToolbarArrow("${getString(R.string.circle_breath_rate)}$titleName")
                url = ApiConstant.HRQ_URL + mid
            }
            getString(R.string.circle_body_temperature) -> {
                setToolbarArrow("${getString(R.string.circle_body_temperature)}$titleName")
                url = ApiConstant.HTQ_URL + mid
            }
            getString(R.string.circle_sleep) -> {
                setToolbarArrow("${getString(R.string.circle_sleep)}$titleName")
                url = ApiConstant.HSlQ_URL + mid
            }
            getString(R.string.circle_step_count) -> {
                setToolbarArrow("${getString(R.string.circle_step_count)}$titleName")
                url = ApiConstant.HStQ_URL + mid
            }

            // --------------------------------------------------------------------
            getString(R.string.circle_go_body) -> {
                setToolbarArrow("${getString(R.string.circle_go_body)}$titleName")
                url = "${ApiConstant.WEB_URL}${ApiConstant.ID_GO_BODY}$id"
            }
            getString(R.string.circle_health_rapid_test) -> {
                setToolbarArrow("${getString(R.string.circle_health_rapid_test)}$titleName")
                url = id?.aesEncrypt()?.let {
                    "${ApiConstant.QUICK_SIEVE_URL}${URLEncoder.encode(it, "UTF-8")}"
                } ?: ApiConstant.QUICK_SIEVE_URL
            }
            getString(R.string.circle_vessel_stiffness) -> {
                setToolbarArrow("${getString(R.string.circle_vessel_stiffness)}$titleName")
                url = "${ApiConstant.WEB_URL}${ApiConstant.ID_VESSEL_STIFFNESS}$id"
            }
            getString(R.string.circle_physical_examination) -> {
                setToolbarArrow("${getString(R.string.circle_physical_examination)}$titleName")
                binding.apply {
                    webView.visibility = View.GONE
                    ivNoData.visibility = View.VISIBLE
                    tvNoData.visibility = View.VISIBLE
                }
            }
            getString(R.string.circle_eecp) -> {
                setToolbarArrow("${getString(R.string.circle_eecp)}$titleName")
                url = "${ApiConstant.WEB_URL}${ApiConstant.ID_EECP}$id"
            }
            getString(R.string.pre_dia_question) -> {
                setToolbarHome(getString(R.string.pre_dia_question))
                url = "${ApiConstant.QUEST_URL}${SharedPreferencesUtil.instances.getOrder()}"
            }
            getString(R.string.pre_back_question) -> {
                setToolbarArrow(getString(R.string.pre_dia_question))
                url = "${ApiConstant.QUEST_URL}${SharedPreferencesUtil.instances.getOrder()}"
            }
            getString(R.string.pre_video_question) -> {
                setToolbarArrow(getString(R.string.pre_dia_question))
                url = "${ApiConstant.QUEST_VIDEO_URL}${SharedPreferencesUtil.instances.getOrder()}"
            }
        }

        Log.d(TAG, "url: $url")
        setupWeb(url)
    }

    private fun setupWeb(url: String) {

        binding.webView.apply {

            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            loadUrl(url)
        }
    }
}