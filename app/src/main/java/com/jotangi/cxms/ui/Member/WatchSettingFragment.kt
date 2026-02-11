package com.jotangi.cxms.ui.Member

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentWatchSettingBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtils
import com.jotangi.cxms.utils.DialogUtils.cameraListener
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.yucheng.ycbtsdk.YCBTClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WatchSettingFragment : BaseFragment() {

    private lateinit var binding: FragmentWatchSettingBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val apiRepository: WatchApiRepository by lazy { WatchApiRepository() }
    private var isConnect = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDeviceInfo()
        initApi()
        initView()
        initHandler()
    }

    private fun getDeviceInfo() {

        WatchUtils.instance.getDeviceInfo { code, ratio, resultMap ->

            Log.w(TAG, "resultMap: $resultMap")

            if (resultMap != null) {

                val dataObj = resultMap["data"]

                if (dataObj is Map<*, *>) {

                    val level: Int = dataObj["deviceBatteryValue"] as Int
                    Log.w(TAG, "電源: $level")
                    isConnect = true

                    context?.let {
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.batteryView.power = level
                            binding.wrBt.text = binding.batteryView.power.toString() + " %"
                        }

                        NotifyCapable()
                    }
                }
            } else {

                CoroutineScope(Dispatchers.IO).launch {
                    WatchUtils.instance.disconnectBle()
                    context?.let {
                        watchViewModel.setIsBleConnect(false)
                    }
                    SharedPreferencesUtil.instances.setWatchMac(null)
                    SharedPreferencesUtil.instances.setWatchName(null)
                }

                context?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        showErrorMsgBack("藍牙連線失敗，請重新連線")
                    }
                }
            }
        }
    }

    private fun initApi() {

        val name = SharedPreferencesUtil.instances.getWatchName()
        val mac = SharedPreferencesUtil.instances.getWatchMac()
        Log.w(TAG, "name: $name mac: $mac")

        if (name.isNullOrBlank() || mac.isNullOrBlank()) {
            watchViewModel.setIsBleConnect(false)
            return
        }

        lifecycleScope.launch {

            val response = apiRepository.getWarrantyinfo(name)

            if (response.code == "0x0200" && !response.data.isNullOrEmpty()) {
                response.data?.let {
                    it[0].startTime
                    binding.tvWarrantyTime.text = "${it[0].startTime} 到 ${it[0].endTime}"
                }
            }
        }
    }

    private fun initView() {

        setToolbarArrow("手錶裝置設定")

        binding.apply {

            // M22JA 0A33
            wrName.text = SharedPreferencesUtil.instances.getWatchName()
            //正在充電就把這個設為true 沒有就false
//            batteryView.setCharge(true)
            // F4:ID:38:I9:0D
            wrId.text = SharedPreferencesUtil.instances.getWatchMac()

            // 右手佩戴模式
            handWearSwitch.isChecked = SharedPreferencesUtil.instances.getWatchWear()
            // 時間格式
            if (SharedPreferencesUtil.instances.getWatchTimeFormat() == "24") {
                hourTypeContentTextView.setText(R.string.hour24)
            } else {
                hourTypeContentTextView.setText(R.string.hour12)
            }
            //  監測頻率
            tvMonitorFrequencyValue.text = SharedPreferencesUtil.instances.getWatchFrequency()
        }
    }

    private fun initHandler() {

        binding.apply {

            // 解除綁定
            btnUnbindWatch.setOnClickListener {
                doRemoveDevice()
            }

            // 右手佩戴模式
            handWearSwitch.setOnClickListener {

                if (!isConnect) {
                    showNotConnected()
                    return@setOnClickListener
                }

                SharedPreferencesUtil.instances.setWatchWear(handWearSwitch.isChecked)

                WatchUtils.instance.settingHandWear(
                    handWearSwitch.isChecked
                ) { code, ratio, resultMap ->
                    Log.d(TAG, "code: $code ratio: $ratio resultMap: $resultMap")
                }
            }

            // 時間格式
            tvHourTypeTitle.setOnClickListener {

                if (!isConnect) {
                    showNotConnected()
                    return@setOnClickListener
                }

                settingTime()
            }

            // 監測頻率
            tvMonitorFrequencyTitle.setOnClickListener {

                if (!isConnect) {
                    showNotConnected()
                    return@setOnClickListener
                }

                DialogUtils().showFrequency(
                    requireActivity()
                ) { minute -> frequencySetting(minute) }
            }

            // 通知設定
            tvNotifySettingTitle.setOnClickListener {

                if (!isConnect) {
                    showNotConnected()
                    return@setOnClickListener
                }
                findNavController().navigate(WatchSettingFragmentDirections.actionNavWatchInfoToNavNotificationSetting())
            }
        }
    }

    private fun showNotConnected() {
        Toast.makeText(requireContext(), "藍牙尚未連線成功", Toast.LENGTH_SHORT).show()
    }

    private fun doRemoveDevice() {
        showProgress()
        CoroutineScope(Dispatchers.IO).launch {
            WatchUtils.instance.disconnectBle()
            watchViewModel.setIsBleConnect(false)
            SharedPreferencesUtil.instances.setWatchMac(null)
            SharedPreferencesUtil.instances.setWatchName(null)
            Thread.sleep(2000)

            CoroutineScope(Dispatchers.Main).launch {
                closeProgress()
                findNavController().navigate(
                    WatchSettingFragmentDirections
                        .actionWatchSettingFragmentToWatchListFragment()
                )
            }
        }
    }

    private fun NotifyCapable() {

        if (!NotificationManagerCompat.getEnabledListenerPackages(requireContext())
                .contains(requireActivity().packageName)
        ) {

            if (!getStatus("isInit")) {
                val list = listOf(
                    "通話", "未接電話", "來電", "未接來電",
                    "messaging", "gm", "calendar", "wechat", "qq",
                    "weibo", "facebook", "twitter", "messenger", "whatsapp",
                    "linkedin", "instagram", "skype", "line", "snapchat",
                    "appnotif"
                )

                for (element in list) {
                    setStatus(element, true)
                }
                setStatus("isInit", true)
            }

            context?.let {

                CoroutineScope(Dispatchers.Main).launch {

                    DialogUtils().showMultiple(
                        requireActivity(),
                        "權限通知",
                        "是否開啟訊息通知權限",
                        "開啟",
                        "關閉",
                        object : DialogUtils.OnMultipleClickListener {
                            override fun onOk() {

                                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                requireContext().startActivity(intent)
                            }

                            override fun onCancel() {
                            }

                        }
                    )
                }
            }
        }
    }

    private fun getStatus(key: String): Boolean {
        return SharedPreferencesUtil.instances.getNotifyStatus(key)
    }

    private fun setStatus(key: String, value: Boolean) {
        SharedPreferencesUtil.instances.setNotifyStatus(key, value)
    }

    private fun frequencySetting(minute: Int) {

        showProgress()

        var isTaskOk = true

        CoroutineScope(Dispatchers.IO).launch {
            WatchUtils.instance.settingHeartMonitor(
                0x01, minute
            ) { i: Int, fl: Float, hashMap: HashMap<Any, Any> ->
                Log.d(TAG, "settingHeartMonitor - i: $i fl: $fl hashMap: $hashMap")
                if (i != 0) {
                    isTaskOk = false
                }

                WatchUtils.instance.settingTemperatureMonitor(
                    true, minute
                ) { i: Int, fl: Float, hashMap: HashMap<Any, Any> ->
                    Log.d(TAG, "settingTemperatureMonitor - i: $i fl: $fl hashMap: $hashMap")
                    if (i != 0) {
                        isTaskOk = false
                    }

                    WatchUtils.instance.settingBloodOxygenModeMonitor(
                        true,
                        minute
                    ) { i: Int, fl: Float, hashMap: HashMap<Any, Any> ->

                        Log.d(TAG, "Oxygen - i: $i fl: $fl hashMap: $hashMap")
                        if (i != -4) {
                            isTaskOk = false
                        }

                        if (isTaskOk) {

                            SharedPreferencesUtil.instances.setWatchFrequency("${minute}分鐘")
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.tvMonitorFrequencyValue.text = "${minute}分鐘"
                            }
                        }

                        closeProgress()
                    }
                }
            }
        }
    }

    private fun settingTime() {
        DialogUtils().hoursDialog(
            activity,
            getString(R.string.hour24),
            getString(R.string.hour12),
            object : cameraListener {
                override fun onTake() {
                    YCBTClient.settingUnit(0, 0, 0, 0) { i, fl, hashMap ->
                        if (i == 0) {
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.hourTypeContentTextView.setText(R.string.hour24)
                                SharedPreferencesUtil.instances.setWatchTimeFormat("24")
                            }
                        }
                    }
                }

                override fun onOpen() {
                    YCBTClient.settingUnit(0, 0, 0, 1) { i, fl, hashMap ->
                        if (i == 0) {
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.hourTypeContentTextView.setText(R.string.hour12)
                                SharedPreferencesUtil.instances.setWatchTimeFormat("12")
                            }
                        }
                    }
                }

                override fun onCancel() {
                    DialogUtils.closeDialog()
                }
            }
        )
    }
}