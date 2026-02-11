package com.jotangi.cxms.ui.home.watch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.Module.GlobalVariable
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentEcgDetailBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.AppUtils
import com.jotangi.cxms.utils.DialogUtils
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apirequest.ECGRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.EcgListRequest
import com.jotangi.cxms.utils.smartwatch.apiresponse.EcgData
import com.yucheng.ycbtsdk.AITools
import com.yucheng.ycbtsdk.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EcgDetailFragment : BaseFragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentEcgDetailBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var ecgAdapter: EcgAdapter

    // 變動清單
    private var dataList = arrayListOf<EcgData>()

    // 基礎清單
    private var b_dataList = arrayListOf<EcgData>()

    protected val apiRepository: WatchApiRepository by lazy { WatchApiRepository() }
    private val memberId = SharedPreferencesUtil.instances.getAccountId()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEcgDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarArrow("心電圖測量紀錄")

        ecgAdapter = EcgAdapter(dataList)

        binding.rvEcg.apply {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = ecgAdapter

            ecgAdapter.ecgItemClick = {

                if (it.hr != Constants.DATATYPE.Real_UploadECG) {

                    GlobalVariable.setEcgData(it)
                    findNavController().navigate(R.id.ecgSixLineFragment)

                } else {

                    DialogUtils.showProgress(requireActivity())

                    // 类型 0 已上传字节数 41733 CRC16 52700
                    // 0 ~ 239，最後一包 07200F0000F00005A30000DCCD0695。 共239*60+15=14355
//                    WatchUtils.instance.collectHistoryDataWithTimestamp(
//                        0x00,
//                        712277093
//                    ) { i: Int, fl: Float, hashMap: HashMap<Any, Any> ->
//
//                        processEcgByte(hashMap["data"] as ByteArray)
//                    }

                    WatchUtils.instance.collectEcgDataWithTimestamp(it.ecgValue!!.toLong())
                    { i: Int, fl: Float, hashMap: HashMap<Any, Any> ->
                        processEcgByte(
                            hashMap["data"] as ByteArray,
                            it.ecgStartTime.toString(),
                            it.ecgValue.toString()
                        )
                    }
                }
            }
        }

        watchViewModel.last7EcgData.observe(viewLifecycleOwner) { datas ->

            dataList.clear()
            b_dataList.clear()
            dataList.addAll(datas)
            b_dataList.addAll(datas)
            binding.pageNodata.pageEmpty.apply {
                visibility = if (dataList.size == 0)
                    View.VISIBLE
                else
                    View.INVISIBLE
            }
            ecgAdapter.notifyDataSetChanged()

            watchConnectionStatus()
        }

        binding.searchView.setOnQueryTextListener(this)
    }

    private fun processEcgByte(dataByte: ByteArray, startTime: String, sendTime: String) {
        Log.d(TAG, "startTime: $startTime")

        AITools.getInstance().init()
        val dataArray = AITools.getInstance().ecgRealWaveFiltering(dataByte) as List<Int>
        Log.d(TAG, "dataArray: $dataArray")
        Log.d(TAG, "dataArray.size: ${dataArray.size}")

        val buffer = StringBuffer()
        for (i in dataArray.indices step 3) {
            buffer.append("${processWatchData(dataArray[i])},")
        }
        buffer.setLength(buffer.length - 1)

        val startTimeValue = WatchUtils.instance.clipTimeFormatSecond(startTime)
        Log.d(TAG, "startTimeValue: $startTimeValue")
        val bufferValue = buffer.toString()
        Log.d(TAG, "bufferValue: $bufferValue")
        val hrValue = AITools.getInstance().heart
        Log.d(TAG, "hrValue: $hrValue")
        val hrvValue = AITools.getInstance().getHRV()

        // 上傳資料
        CoroutineScope(Dispatchers.IO).launch {
            val resp = apiRepository.ecgUpload(
                ECGRequest(
                    memberId!!,
                    startTimeValue,
                    bufferValue,
                    hrValue,
                    0,
                    0,
                    hrvValue
                )
            )
            if (resp.code == "0x0200") {

                Log.d(TAG, "ecg upload success")

                WatchUtils.instance.deleteHistoryListData(
                    0x00,
                    sendTime.toLong()
                ) { i: Int, fl: Float, hashMap: HashMap<Any, Any> ->
                    Log.d(TAG, "刪除結束: ")
                    getECG()
                }

                GlobalVariable.setEcgData(
                    EcgData(
                        "",
                        bufferValue,
                        hrValue,
                        0,
                        0,
                        hrvValue
                    )
                )

                CoroutineScope(Dispatchers.Main).launch {
                    DialogUtils.closeProgress()
                    findNavController().navigate(R.id.ecgSixLineFragment)
                }

            } else {

                Log.d(TAG, "ecg upload failed ${resp.responseMessage}")

                CoroutineScope(Dispatchers.Main).launch {
                    DialogUtils.closeProgress()
                }
            }
        }
    }

    private fun processWatchData(num: Int): Int {
        var value = num * 4 / 1000
        value = if (value > 155) 155 else value
        value = if (value < -155) -155 else value
        return value
    }

    private fun watchConnectionStatus() {

        val mac = SharedPreferencesUtil.instances.getWatchMac()

        if (mac != null) {

            if (WatchUtils.instance.isBleConnect()) {

                watchViewModel.setIsBleConnect(true)
                watchEcgList()
            } else {

                watchViewModel.setIsBleConnect(false)
                WatchUtils.instance.connectBle(mac) { code ->

                    if (code == Constants.CODE.Code_OK) {
                        watchEcgList()
                    }
                }
            }
        }
    }

    private fun watchEcgList() {

        // collectSendTime=712277093, collectStartTime=1658933093000， time: 2022-07-27 22:44:53
        // collectSendTime=712750870, collectStartTime=1659406870000
        WatchUtils.instance.collectHistoryListData(0x00) { i: Int, fl: Float, hashMap: HashMap<Any, Any> ->

            Log.d(TAG, "hashMap: $hashMap")
            if (hashMap != null) {
                val data = hashMap["data"] as List<*>
                var itemHashMap: HashMap<String, String>
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                var collectStartTime: String
                var processList = listOf<EcgData>()

                data.forEach {
                    itemHashMap = it as HashMap<String, String>

                    collectStartTime = sdf.format(
                        Date(
                            itemHashMap["collectStartTime"].toString().toLong()
                        )
                    )
                    Log.w(TAG, "手錶資料 時間: ${collectStartTime}")

                    dataList.add(
                        EcgData(
                            collectStartTime,
                            itemHashMap["collectSendTime"].toString(),
                            Constants.DATATYPE.Real_UploadECG,
                            0,
                            0,
                            0
                        )
                    )
                }

                processList = dataList
                processList = processList.sortedByDescending { it.ecgStartTime }

                dataList.clear()
                dataList.addAll(processList)
                b_dataList.clear()
                b_dataList.addAll(dataList)

                CoroutineScope(Dispatchers.Main).launch {

                    ecgAdapter.notifyDataSetChanged()

                    binding.pageNodata.pageEmpty.visibility =
                        if (dataList.size == 0) View.VISIBLE else View.INVISIBLE
                }

            } else {
                Log.e(TAG, "手錶無 ECG 資料")
            }
        }
    }

    private fun getECG() {

        CoroutineScope(Dispatchers.IO).launch {

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -3)
            val startTime = sdf.format(calendar.time)
            val endTime = sdf.format(Date())

            Log.w(TAG, AppUtils.logTitle("EcgListRequest"))
            Log.d(TAG, "memberId: $memberId")
            Log.d(TAG, "startTime: $startTime")
            Log.d(TAG, "endTime: $endTime")
            watchViewModel.getECG(
                EcgListRequest(
                    memberId!!,
                    startTime,
                    endTime
                )
            )
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            Log.d("searchbar", newText)
            if (b_dataList != null) {
                var ndataList = b_dataList.filter { it.ecgStartTime!!.startsWith(newText) }
                var sortedData = ndataList.sortedByDescending { it.ecgStartTime }
                dataList.clear()
                dataList.addAll(sortedData)
                ecgAdapter.notifyDataSetChanged()
            }
        }
        return true
    }
}