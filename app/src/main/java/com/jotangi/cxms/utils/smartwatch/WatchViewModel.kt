package com.jotangi.cxms.utils.smartwatch

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jotangi.cxms.ui.ecg.EcgDataCallback
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.apirequest.EcgListRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.GetBmdRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.TemperatureListRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.WatchCommonRequest
import com.jotangi.cxms.utils.smartwatch.apiresponse.*
import com.jotangi.cxms.utils.smartwatch.model.*
import com.yucheng.ycbtsdk.AITools
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class WatchViewModel(var watchApiRepository: WatchApiRepository) : ViewModel() {

    private val TAG: String = "${javaClass.simpleName}(TAG)"

    val isBleConnect = MutableLiveData(false)

    val lastHeartRateData = MediatorLiveData<List<HeartRateData>>()
    val lastOxygenData = MediatorLiveData<List<OxygenData>>()
    val lastTemperatureData = MediatorLiveData<List<TemperatureData>>()

    //    val lastRespiratoryRateData = MediatorLiveData<List<RespiratoryRateData>>()
    val lastBreathRateData = MediatorLiveData<List<BreathRateData>>()
    val lastBpData = MediatorLiveData<List<BPData>>()

    // 睡眠
    val lastSleepStartTime = MediatorLiveData<String>()
    val lastSleepEndTime = MediatorLiveData<String>()
    val totalSleepHour = MediatorLiveData<Int>()
    val totalSleepMinute = MediatorLiveData<Int>()
    val daySleepDetailData = MediatorLiveData<List<SleepDetailData>>()
    val lastSleepQuality = MediatorLiveData<Int>()
    val day7TrendDate = MediatorLiveData<LinkedList<String>>()
    val day7TrendSleepTotal = MediatorLiveData<IntArray>()
    val day7TrendSleepDeep = MediatorLiveData<IntArray>()

    val daySleepData = MediatorLiveData<List<SleepData>>()
    val dayFootStepsData = MediatorLiveData<List<GetStepsDataBean>>()
    val last7FootStepsData = MediatorLiveData<List<GetStepsDataBean>>()
    val lastFootStepsData = MediatorLiveData<GetStepsDataBean>()
    val dayTotalSteps = MediatorLiveData<Int?>()
    val dayTotalCalories = MediatorLiveData<Int?>()
    val dayTotalMeters = MediatorLiveData<Int?>()
    val watchBatteryLevel = MutableLiveData(100)
    val last7EcgData = MediatorLiveData<List<EcgData>>()
    val headshotPath = MediatorLiveData<String>()

    // 黃斑部色素
    val getMpodList = MediatorLiveData<List<GetMpodDataBean>>()
    val getBp2List = MediatorLiveData<List<GetBp2DataBean>>()
    val getKcalList = MediatorLiveData<List<GetKcalDataBean>>()

    // 骨質密度
    val getBmdList = MediatorLiveData<List<GetBmdDataBean>>()
    val getStepsKcalList = MediatorLiveData<List<GetStepsDataBean>>()
    val kcal7DayValueList = MediatorLiveData<List<String>>()
    val kcal7DayDateList = MediatorLiveData<List<String>>()



    fun refreshHeadShotPath() {
        headshotPath.postValue(SharedPreferencesUtil.instances.getAccountHeadShot())
    }

    fun setIsBleConnect(boolean: Boolean) {
        isBleConnect.postValue(boolean)
    }

    suspend fun getHeartRate(heartRateRequest: HeartRateRequest) {
        var data = watchApiRepository.getHeartRate(heartRateRequest).data
        if (data != null && data.isNotEmpty()) {
            val list = data.sortedByDescending { it.heartStartTime }
            if (list != null && list.isNotEmpty()) {
                lastHeartRateData.postValue(list)
            }
        }
    }

    suspend fun getBp(bpRequest: BPRequest) {
        var data = watchApiRepository.getBP(bpRequest).data
        if (data != null && data.isNotEmpty()) {
            val list = data.sortedByDescending { it.bloodStartTime }
            if (list != null && list.isNotEmpty()) {
                lastBpData.postValue(list)
            }
        }
    }

    suspend fun getOxygen(oxygenRequest: OxygenRequest) {
        var data = watchApiRepository.getOxygen(oxygenRequest).data
        if (data != null && data.isNotEmpty()) {
            val list = data.sortedByDescending { it.startTime }
            if (list != null && list.isNotEmpty()) {
                lastOxygenData.postValue(list)
            }
        }
    }

    suspend fun getBreathRate(breathRateRequest: BreathRateRequest) {
        var data = watchApiRepository.getBreathRate(breathRateRequest).data
        if (data != null && data.isNotEmpty()) {
            val list = data.sortedByDescending { it.startTime }
            if (list != null && list.isNotEmpty()) {
                lastBreathRateData.postValue(list)
            }
        }
    }

    suspend fun getTemperature(temperatureListRequest: TemperatureListRequest) {
        var data = watchApiRepository.getTemperature(temperatureListRequest).data
        if (data != null && data.isNotEmpty()) {
            val realTemList =
                data.filter { 33.99F < it.temperature.toFloat() && it.temperature.toFloat() < 41.01F }
            val list = realTemList.sortedByDescending { it.startTime }
            if (list.isNotEmpty()) {
                lastTemperatureData.postValue(list)
            }
        }
    }

    suspend fun getSleep(sleepRequest: SleepRequest) {

        var data = watchApiRepository.getSleep(sleepRequest).data

        if (!data.isNullOrEmpty()) {

            data = data.sortedByDescending { it.startTime }
            Log.w(TAG, "sleep data: $data")

            /**
             * [SleepData(startTime=2022-07-10 04:30:08, endTime=2022-07-10 08:33:24, deepSleepCount=1, lightSleepCount=10, deepSleepTotal=47, lightSleepTotal=195),
             * SleepData(startTime=2022-07-07 04:25:26, endTime=2022-07-07 08:23:08, deepSleepCount=3, lightSleepCount=8, deepSleepTotal=71, lightSleepTotal=166),
             * SleepData(startTime=2022-07-07 01:35:35, endTime=2022-07-07 03:47:28, deepSleepCount=1, lightSleepCount=5, deepSleepTotal=39, lightSleepTotal=92),
             * SleepData(startTime=2022-07-06 07:01:31, endTime=2022-07-06 09:02:01, deepSleepCount=0, lightSleepCount=6, deepSleepTotal=0, lightSleepTotal=120),
             * SleepData(startTime=2022-07-06 00:49:41, endTime=2022-07-06 06:44:20, deepSleepCount=3, lightSleepCount=13, deepSleepTotal=68, lightSleepTotal=286)]
             * deepSleepTotal、lightSleepTotal:分鐘
             */

            val sleepList = arrayListOf<SleepData>()
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            var baseDate = data[0].startTime.toString().split(" ")[0] + " 17:00:00"
            if (baseDate > data[0].startTime.toString()) {
                calendar.time = sdf.parse(baseDate)
                calendar.add(Calendar.DATE, -1)
                baseDate = sdf.format(calendar.time)
            }

            var startTime: String
            var endTime = data[0].endTime.toString()
            var deepSleepTotal = 0
            var lightSleepTotal = 0

            for (i in data.indices) {

                if (baseDate > data[i].startTime.toString()) {

                    startTime = data[i - 1].startTime.toString()
                    sleepList.add(
                        SleepData(
                            startTime,
                            endTime,
                            "",
                            "",
                            deepSleepTotal.toString(),
                            lightSleepTotal.toString()
                        )
                    )


                    baseDate = data[i].startTime.toString().split(" ")[0] + " 17:00:00"
                    if (baseDate > data[i].startTime.toString()) {
                        calendar.time = sdf.parse(baseDate)
                        calendar.add(Calendar.DATE, -1)
                        baseDate = sdf.format(calendar.time)
                    }
                    endTime = data[i].endTime.toString()
                    deepSleepTotal = 0
                    lightSleepTotal = 0
                }

                deepSleepTotal += data[i].deepSleepTotal?.toInt() ?: 0
                lightSleepTotal += data[i].lightSleepTotal?.toInt() ?: 0
            }
            startTime = data[data.size - 1].startTime.toString()
            sleepList.add(
                SleepData(
                    startTime,
                    endTime,
                    "",
                    "",
                    deepSleepTotal.toString(),
                    lightSleepTotal.toString()
                )
            )
            Log.w(TAG, "sleepList: $sleepList")

            // 睡眠清單
            daySleepData.postValue(sleepList.toList())

            val baseDate2 = findBaseDate(data[0].endTime!!)

            val theDay = data.filter { it.startTime!! > baseDate2 }
            Log.d(TAG, "最近一天列表: $theDay")

            lastSleepStartTime.postValue(theDay.last().startTime!!)
            lastSleepEndTime.postValue(theDay[0].endTime!!)

            var minute = 0
            for (i in theDay.indices) {
                minute += theDay[i].deepSleepTotal!!.toInt() + theDay[i].lightSleepTotal!!.toInt()
            }
            totalSleepHour.postValue(minute / 60)
            totalSleepMinute.postValue(minute % 60)
        }
    }

    suspend fun getStepsKcal(request: WatchCommonRequest) {
        val list = watchApiRepository.getGetSteps(request).data
        if (list.isNullOrEmpty())
            getStepsKcalList.postValue(listOf())
        else
            getStepsKcalList.postValue(list.sortedByDescending { it.sportStartTime })
    }

    suspend fun getKcal(request: WatchCommonRequest) {

        val list = watchApiRepository.getKcal(request).data

        if (list.isNullOrEmpty() && getStepsKcalList.value.isNullOrEmpty()) {
            getKcalList.postValue(listOf())
            kcal7DayValueList.postValue(listOf("0", "0", "0", "0", "0", "0", "0"))
        } else {

            val arrayList = arrayListOf<GetKcalDataBean>()
            list?.let {
                arrayList.addAll(list)
            }

            getStepsKcalList.value?.let {
                for (i in it.indices) {
                    arrayList.add(
                        GetKcalDataBean(it[i].sportStartTime, it[i].sportCalorie)
                    )
                }
            }

            val array = arrayList.sortedByDescending { it.startTime }
            arrayList.clear()

            var time = "--"
            var clip: String
            var indexArray: GetKcalDataBean
            for (i in array.indices) {
                array[i].startTime?.let {
                    clip = WatchUtils.instance.clipTimeToYMD(it)
                    if (time != clip) {
                        time = clip
                        arrayList.add(array[i])
                    } else {
                        indexArray = arrayList[arrayList.size - 1]
                        indexArray.KCAL =
                            (indexArray.KCAL!!.toInt() + array[i].KCAL!!.toInt()).toString()
                    }
                }
            }

            var dateTime: String
            val dateList = WatchUtils.instance.current7Day()
            val valueList = arrayListOf("0", "0", "0", "0", "0", "0", "0")

            for (i in arrayList.indices) {
                dateTime = WatchUtils.instance.clipTimeToYMD(arrayList[i].startTime!!)
                for (j in dateList.indices) {
                    if (dateTime == dateList[j]) {
                        valueList[j] = arrayList[i].KCAL!!
                        break
                    }
                }
                if (i == 6) {
                    break
                }
            }

            val mdList = ArrayList<String>()
            dateList.forEach {
                mdList.add(WatchUtils.instance.clipYmdToMd(it))
            }

            kcal7DayValueList.postValue(valueList)
            kcal7DayDateList.postValue(mdList)
            getKcalList.postValue(arrayList)
        }
    }

    suspend fun getGetSteps(request: WatchCommonRequest) {

        var data = watchApiRepository.getGetSteps(request).data
        /**
        [
        FootStepsData(sportStartTime=2022-05-31 12:00:00, sportEndTime=2022-05-31 12:30:00, sportStep=112, sportCalorie=5, sportDistance=102),
        FootStepsData(sportStartTime=2022-05-31 12:30:00, sportEndTime=2022-05-31 13:00:00, sportStep=1674, sportCalorie=68, sportDistance=1188),
        FootStepsData(sportStartTime=2022-05-31 14:00:00, sportEndTime=2022-05-31 14:30:00, sportStep=1528, sportCalorie=62, sportDistance=1085)
        ]
         */

        if (!data.isNullOrEmpty()) {

            data = data.sortedByDescending { it.sportStartTime }

            var arrayDayData = ArrayList<GetStepsDataBean>()
            var dayBase = "null"
            var sportStartTime = ""
            var sportEndTime = ""
            var sportStep = 0
            var sportCalorie = 0
            var sportDistance = 0

            for (i in data.indices) {

                if (!data[i].sportStartTime.toString().contains(dayBase)) {

                    dayBase = data[i].sportStartTime.toString().split(" ")[0]

                    if (i != 0) {
                        sportStartTime = data[i - 1].sportStartTime.toString()
//                        Log.d(TAG, "i: $i sportStartTime: $sportStartTime sportEndTime: $sportEndTime sportStep: $sportStep sportCalorie: $sportCalorie sportDistance: $sportDistance")
                        arrayDayData.add(
                            GetStepsDataBean(
                                sportStartTime,
                                sportEndTime,
                                sportStep.toString(),
                                sportCalorie.toString(),
                                sportDistance.toString()
                            )
                        )
                    }

                    sportEndTime = data[i].sportEndTime.toString()
                    sportStep = data[i].sportStep?.toInt() ?: 0
                    sportCalorie = data[i].sportCalorie?.toInt() ?: 0
                    sportDistance = data[i].sportDistance?.toInt() ?: 0

                } else {

                    sportStep += data[i].sportStep?.toInt() ?: 0
                    sportCalorie += data[i].sportCalorie?.toInt() ?: 0
                    sportDistance += data[i].sportDistance?.toInt() ?: 0

                }
            }
            sportStartTime = data[data.size - 1].sportStartTime.toString()
//            Log.d(TAG, "data.size: ${data.size} sportStartTime: $sportStartTime sportEndTime: $sportEndTime sportStep: $sportStep sportCalorie: $sportCalorie sportDistance: $sportDistance")
            arrayDayData.add(
                GetStepsDataBean(
                    sportStartTime,
                    sportEndTime,
                    sportStep.toString(),
                    sportCalorie.toString(),
                    sportDistance.toString()
                )
            )
            Log.w(TAG, "arrayDayData: $arrayDayData")

            // 清單
            dayFootStepsData.postValue(arrayDayData.toList())
            // 最新一天
            dayTotalSteps.postValue(arrayDayData[0].sportStep?.toInt() ?: 0)
            dayTotalCalories.postValue(arrayDayData[0].sportCalorie?.toInt() ?: 0)
            dayTotalMeters.postValue(arrayDayData[0].sportDistance?.toInt() ?: 0)
            // 近7天
            last7FootStepsData.postValue(arrayDayData.take(7).toList())
            lastFootStepsData.postValue(arrayDayData[0])
        }
    }

    suspend fun getECG(ecgListRequest: EcgListRequest) {
        val data = watchApiRepository.getECG(ecgListRequest).data
        if (data != null && data.isNotEmpty()) {
            val list = data.sortedByDescending { it.ecgStartTime }
            last7EcgData.postValue(list)
        }
    }

    suspend fun getBp2(watchCommonRequest: WatchCommonRequest) {
        watchApiRepository.getBp2(watchCommonRequest).data?.let { list ->
            if (list.isNotEmpty()) {
                getBp2List.postValue(list.sortedByDescending { it.bloodStartTime })
            }
        }
    }

    suspend fun getMPOD(watchCommonRequest: WatchCommonRequest) {
        val data = watchApiRepository.getMPOD(watchCommonRequest).data
        data?.let {
            if (data.isNotEmpty()) {
                val list = data.sortedByDescending { it.mpodStartTime }
                getMpodList.postValue(list)
            }
        }
    }

    suspend fun getBMD(getBmdRequest: GetBmdRequest) {
        val data = watchApiRepository.getBMD(getBmdRequest).data
        data?.let {
            if (data.isNotEmpty()) {
                val list = data.sortedByDescending { it.startTime }
                getBmdList.postValue(list)
            }
        }
    }

//    suspend fun getRespiratoryRate(respiratoryRateListRequest: RespiratoryRateListRequest){
//        var data = watchApiRepository.getRespiratoryRate(respiratoryRateListRequest).data
//        if (data != null && data.isNotEmpty()) {
//            val list = data.sortedByDescending { it.startTime }
//            if (list != null && list.isNotEmpty()) {
//                lastRespiratoryRateData.postValue(list)
//            }
//        }
//    }

    fun getWatchInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            val state = YCBTClient.connectState()
            if (state == Constants.BLEState.ReadWriteOK) {
                doGetDeviceInfo()
            } else {
                val mac = SharedPreferencesUtil.instances.getWatchMac()
                if (mac != null) {
                    YCBTClient.disconnectBle()
                    YCBTClient.connectBle(mac) { code ->
                        if (code == Constants.CODE.Code_OK) {
                            doGetDeviceInfo()
                        }
                    }
                }
            }
        }
    }

    private fun doGetDeviceInfo() {
        YCBTClient.getDeviceInfo { code, ratio, resultMap ->
            if (resultMap != null) {
                val dataObj = resultMap.get("data")
                if (dataObj is Map<*, *>) {
                    val level: Int = dataObj.get("deviceBatteryValue") as Int
                    setWatchBatteryLevel(level)
                }
            }
        }
    }

    fun setWatchBatteryLevel(level: Int) {
        watchBatteryLevel.postValue(level)
    }

    fun appECGTest(callback: EcgDataCallback.Start) {
        val aitool = AITools.getInstance()
        aitool.init()
        aitool.setAIDiagnosisHRVNormResponse {

        }
        YCBTClient.appEcgTestStart({ i, fl, hashMap ->
        }, { i, hashMap ->
            hashMap.let {
                try {
                    when (i) {
                        Constants.DATATYPE.Real_UploadECG -> {
                            val tData = hashMap["data"] as ArrayList<Int>
                            //Log.d("eee",tData.toString())
                            //var aa = aitool.ecgRealWaveFiltering(listToBytes(tData))
                            for (i in 0 until tData.size) {
                                callback.receiveECG(tData[i].toFloat())
                            }
                        }
                        Constants.DATATYPE.Real_UploadECGHrv -> {
                            val tData = hashMap["data"] as Float
                            callback.receiveHRV(tData.toInt())
                        }
                        Constants.DATATYPE.Real_UploadBlood -> {
                            val heart = hashMap["heartValue"] as Int
                            val tDBP = hashMap["bloodDBP"] as Int
                            val tSBP = hashMap["bloodSBP"] as Int
                            callback.receiveBlood(heart, tDBP, tSBP)
                        }
                        else -> {
                            callback.receiveBadSignal()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun appECGTestEnd(callback: EcgDataCallback.End) {
        try {
            YCBTClient.appEcgTestEnd { i, fl, hashMap ->
                callback.receive(fl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getSleepDetail(sleepRequest: SleepRequest) {

        var data = watchApiRepository.getSleepDetail(sleepRequest).data

        if (!data.isNullOrEmpty()) {

            data = data.sortedByDescending { it.sleepStartTime }
            Log.w(TAG, "SleepDetail data: $data")

            /**
             * [SleepDetailData(sleepStartTime=2022-07-10 07:08:14, sleepType=242, sleepLen=5110),
             * SleepDetailData(sleepStartTime=2022-07-10 06:20:50, sleepType=241, sleepLen=2843),
             * SleepDetailData(sleepStartTime=2022-07-10 04:30:08, sleepType=242, sleepLen=6641),
             * SleepDetailData(sleepStartTime=2022-07-07 07:58:12, sleepType=241, sleepLen=1496),
             * SleepDetailData(sleepStartTime=2022-07-07 07:24:38, sleepType=242, sleepLen=2013),
             * SleepDetailData(sleepStartTime=2022-07-07 06:48:00, sleepType=241, sleepLen=2197),
             * SleepDetailData(sleepStartTime=2022-07-07 04:57:47, sleepType=242, sleepLen=6612),
             * SleepDetailData(sleepStartTime=2022-07-07 04:48:06, sleepType=241, sleepLen=580),
             * SleepDetailData(sleepStartTime=2022-07-07 04:25:26, sleepType=242, sleepLen=1359),
             * SleepDetailData(sleepStartTime=2022-07-07 03:07:56, sleepType=241, sleepLen=2372),
             * SleepDetailData(sleepStartTime=2022-07-07 01:35:35, sleepType=242, sleepLen=5540),
             * SleepDetailData(sleepStartTime=2022-07-06 07:01:31, sleepType=242, sleepLen=7230),
             * SleepDetailData(sleepStartTime=2022-07-06 06:22:06, sleepType=242, sleepLen=1334),
             * SleepDetailData(sleepStartTime=2022-07-06 05:47:50, sleepType=241, sleepLen=2055),
             * SleepDetailData(sleepStartTime=2022-07-06 02:43:27, sleepType=242, sleepLen=11062),
             * SleepDetailData(sleepStartTime=2022-07-06 02:27:52, sleepType=241, sleepLen=934),
             * SleepDetailData(sleepStartTime=2022-07-06 01:51:26, sleepType=242, sleepLen=2185),
             * SleepDetailData(sleepStartTime=2022-07-06 01:32:56, sleepType=241, sleepLen=1109),
             * SleepDetailData(sleepStartTime=2022-07-06 00:49:41, sleepType=242, sleepLen=2594)]
             * sleepType: 242(深睡)、241(淺睡)
             * sleepLen: 秒數
             */

            lastSleepQuality.postValue(qualityNumber(data))

            // 睡眠近7天 表格下方日期列表
            day7TrendDate.postValue(processTrendData())

            processTrendHour(data)
        }
    }

    private fun findBaseDate(lastEndDate: String): String {

        // 2022-07-07 08:23:08
        val timeSlice = lastEndDate.split(" ")
        var baseDate = timeSlice[0] + " 17:00:00"

        if (lastEndDate < baseDate) {

            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(baseDate)
            calendar.time = date as Date
            calendar.add(Calendar.DATE, -1)
            baseDate = sdf.format(calendar.time)
        }
        return baseDate
    }

    private fun processTrendHour(data: List<SleepDetailData>) {

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeSlice = sdf.format(calendar.time).split(" ")
        var checkDate = timeSlice[0] + " 17:00:00"

        // 當天晚上有資料
        if ("17:00:00" < timeSlice[1] && checkDate < data[0].sleepStartTime!!) {

            calendar.time = sdf.parse(checkDate)!!
        } else {

            calendar.time = sdf.parse(checkDate)!!
            calendar.add(Calendar.DATE, -1)
            checkDate = sdf.format(calendar.time)
        }

        val checkDateList = LinkedList<String>()

        for (i in 0 until 7) {
            checkDateList.addFirst(checkDate)
            calendar.add(Calendar.DATE, -1)
            checkDate = sdf.format(calendar.time)
        }
        Log.d(TAG, "checkDateList: $checkDateList")


        val listHourDeep = IntArray(7)
        val listHourTotal = IntArray(7)

        data.forEach {

            when {

                checkDateList[0] < it.sleepStartTime!! &&
                        it.sleepStartTime!! < checkDateList[1] -> {

                    if ("242" == it.sleepType) {
                        listHourTotal[0] += it.sleepLen!!.toInt()
                    } else {
                        listHourDeep[0] += it.sleepLen!!.toInt()
                        listHourTotal[0] += it.sleepLen!!.toInt()
                    }
                }

                checkDateList[1] < it.sleepStartTime!! &&
                        it.sleepStartTime!! < checkDateList[2] -> {

                    if ("242" == it.sleepType) {
                        listHourTotal[1] += it.sleepLen!!.toInt()
                    } else {
                        listHourDeep[1] += it.sleepLen!!.toInt()
                        listHourTotal[1] += it.sleepLen!!.toInt()
                    }
                }

                checkDateList[2] < it.sleepStartTime!! &&
                        it.sleepStartTime!! < checkDateList[3] -> {

                    if ("242" == it.sleepType) {
                        listHourTotal[2] += it.sleepLen!!.toInt()
                    } else {
                        listHourDeep[2] += it.sleepLen!!.toInt()
                        listHourTotal[2] += it.sleepLen!!.toInt()
                    }
                }

                checkDateList[3] < it.sleepStartTime!! &&
                        it.sleepStartTime!! < checkDateList[4] -> {

                    if ("242" == it.sleepType) {
                        listHourTotal[3] += it.sleepLen!!.toInt()
                    } else {
                        listHourDeep[3] += it.sleepLen!!.toInt()
                        listHourTotal[3] += it.sleepLen!!.toInt()
                    }
                }

                checkDateList[4] < it.sleepStartTime!! &&
                        it.sleepStartTime!! < checkDateList[5] -> {

                    if ("242" == it.sleepType) {
                        listHourTotal[4] += it.sleepLen!!.toInt()
                    } else {
                        listHourDeep[4] += it.sleepLen!!.toInt()
                        listHourTotal[4] += it.sleepLen!!.toInt()
                    }
                }

                checkDateList[5] < it.sleepStartTime!! &&
                        it.sleepStartTime!! < checkDateList[6] -> {

                    if ("242" == it.sleepType) {
                        listHourTotal[5] += it.sleepLen!!.toInt()
                    } else {
                        listHourDeep[5] += it.sleepLen!!.toInt()
                        listHourTotal[5] += it.sleepLen!!.toInt()
                    }
                }

                checkDateList[6] < it.sleepStartTime!! -> {

                    if ("242" == it.sleepType) {
                        listHourTotal[6] += it.sleepLen!!.toInt()
                    } else {
                        listHourDeep[6] += it.sleepLen!!.toInt()
                        listHourTotal[6] += it.sleepLen!!.toInt()
                    }
                }
            }
        }

        for (i in listHourDeep.indices) {
            listHourDeep[i] = listHourDeep[i] / 60 / 60
            listHourTotal[i] = listHourTotal[i] / 60 / 60
        }

        Log.d(TAG, "listHourDeep: ${listHourDeep.toCollection(ArrayList())}")
        Log.d(TAG, "listHourTotal: ${listHourTotal.toCollection(ArrayList())}")

        day7TrendSleepDeep.postValue(listHourDeep)
        day7TrendSleepTotal.postValue(listHourTotal)
    }


    private fun qualityNumber(data: List<SleepDetailData>): Int {

        val baseDate = findBaseDate(data[0].sleepStartTime!!)
        Log.d(TAG, "baseDate: $baseDate")

        val theDay = data.filter { it.sleepStartTime!! > baseDate }
        Log.d(TAG, "最近一天列表: $theDay")

        daySleepDetailData.postValue(theDay)

        var seconds = 0
        theDay.forEach { seconds += it.sleepLen?.toInt()!! }

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(theDay[0].sleepStartTime!!)
        calendar.time = date as Date
        calendar.add(Calendar.SECOND, theDay[0].sleepLen!!.toInt())
        val lastDate = sdf.format(calendar.time)

        val diff = (sdf.parse(lastDate)!!.time -
                sdf.parse(theDay.last().sleepStartTime!!)!!.time
                ) / 1000

        return (seconds * 100 / diff).toInt()
    }

    private fun processTrendData(): LinkedList<String> {

        val listDate = LinkedList<String>()

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())

        for (i in 0 until 7) {
            listDate.addFirst(sdf.format(calendar.time))
            calendar.add(Calendar.DATE, -1)
        }
        return listDate
    }
}