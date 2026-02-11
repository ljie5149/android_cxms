package com.jotangi.cxms.utils.smartwatch

import com.jotangi.cxms.Api.AppClientManager
import com.jotangi.cxms.ui.mylittlemin.BaseBookRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.*
import com.jotangi.cxms.utils.smartwatch.apiresponse.*
import com.jotangi.cxms.utils.smartwatch.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class WatchApiRepository {

    suspend fun stepUpload(stepRequest: StepRequest): BaseWristBandResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(stepRequest.memberId)
        map["sportStartTime"] = toRequestBody(stepRequest.sportStartTime)
        map["sportEndTime"] = toRequestBody(stepRequest.sportEndTime)
        map["sportStep"] = toRequestBody(stepRequest.sportStep.toString())
        map["sportCalorie"] = toRequestBody(stepRequest.sportCalorie.toString())
        map["sportDistance"] = toRequestBody(stepRequest.sportDistance.toString())

        return AppClientManager.instance.watchService.stepUpload(map)
    }

    suspend fun sleepUpload(sleepRequest: SleepRequest): BaseWristBandResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(sleepRequest.memberId)
        map["startTime"] = toRequestBody(sleepRequest.startTime)
        map["endTime"] = toRequestBody(sleepRequest.endTime)
        map["deepSleepCount"] = toRequestBody(sleepRequest.deepSleepCount.toString())
        map["lightSleepCount"] = toRequestBody(sleepRequest.lightSleepCount.toString())
        map["deepSleepTotal"] = toRequestBody(sleepRequest.deepSleepTotal.toString())
        map["lightSleepTotal"] = toRequestBody(sleepRequest.lightSleepTotal.toString())
        map["sleepData"] = toRequestBody(sleepRequest.sleepData)

//        map["sleepType"] = toRequestBody(sleepRequest.sleepType)
//        map["sleepStartTime"] = toRequestBody(sleepRequest.startTime)
//        map["sleepLen"] = toRequestBody(sleepRequest.sleepLen)

        return AppClientManager.instance.watchService.sleepUpload(map)
    }

    suspend fun heartRateUpload(heartRateRequest: HeartRateRequest): BaseWristBandResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(heartRateRequest.memberId)
        map["heartStartTime"] = toRequestBody(heartRateRequest.heartStartTime)
        map["heartValue"] = toRequestBody(heartRateRequest.heartValue.toString())
        map["dataType"] = toRequestBody(heartRateRequest.dataType.toString())

        return AppClientManager.instance.watchService.heartRateUpload(map)
    }

    suspend fun bpUpload(bpRequest: BPRequest): BaseWristBandResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(bpRequest.memberId)
        map["bloodStartTime"] = toRequestBody(bpRequest.bloodStartTime)
        map["bloodDBP"] = toRequestBody(bpRequest.bloodDBP.toString())
        map["bloodSBP"] = toRequestBody(bpRequest.bloodSBP.toString())
        map["dataType"] = toRequestBody(bpRequest.dataType.toString())

        return AppClientManager.instance.watchService.bpUpload(map)
    }

    suspend fun ecgUpload(ecgRequest: ECGRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(ecgRequest.memberId)
        map["ecgStartTime"] = toRequestBody(ecgRequest.ecgStartTime)
        map["ecgValue"] = toRequestBody(ecgRequest.ecgValue)
        map["hr"] = toRequestBody(ecgRequest.hr.toString())
        map["dbp"] = toRequestBody(ecgRequest.dbp.toString())
        map["sbp"] = toRequestBody(ecgRequest.sbp.toString())
        map["hrv"] = toRequestBody(ecgRequest.hrv.toString())

        return AppClientManager.instance.watchService.ecgUpload(map)
    }

    suspend fun uploadMpod(uploadMpodRequest: UploadMpodRequest): WatchCommonResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(uploadMpodRequest.memberId)
        map["mpodStartTime"] = toRequestBody(uploadMpodRequest.mpodStartTime)
        map["lefteye"] = toRequestBody(uploadMpodRequest.lefteye)
        map["righteye"] = toRequestBody(uploadMpodRequest.righteye)
        map["dataType"] = toRequestBody(uploadMpodRequest.dataType)

        return AppClientManager.instance.watchService.uploadMpod(map)
    }

    suspend fun bmdUpload(bmdRequest: BmdUploadRequest): WatchCommonResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(bmdRequest.memberId)
        map["startTime"] = toRequestBody(bmdRequest.startTime)
        map["TScore"] = toRequestBody(bmdRequest.TScore)
        map["dataType"] = toRequestBody(bmdRequest.dataType)

        return AppClientManager.instance.watchService.bmdUpload(map)
    }

    suspend fun oxygenUpload(oxygenRequest: OxygenRequest): BaseWristBandResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(oxygenRequest.memberId)
        map["startTime"] = toRequestBody(oxygenRequest.startTime)
        map["OOValue"] = toRequestBody(oxygenRequest.OOValue.toString())
        map["dataType"] = toRequestBody(oxygenRequest.dataType.toString())

        return AppClientManager.instance.watchService.oxygenUpload(map)
    }

    suspend fun temperatureUpload(temperatureRequest: TemperatureRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(temperatureRequest.memberId)
        map["startTime"] = toRequestBody(temperatureRequest.startTime)
        map["temperature"] = toRequestBody(temperatureRequest.temperature.toString())
        map["dataType"] = toRequestBody(temperatureRequest.dataType.toString())

        return AppClientManager.instance.watchService.temperatureUpload(map)
    }

    suspend fun respiratoryRateUpload(respiratoryRateRequest: RespiratoryRateRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["memberId"] = toRequestBody(respiratoryRateRequest.memberId)
        map["startTime"] = toRequestBody(respiratoryRateRequest.startTime)
        map["respiratoryrate"] = toRequestBody(respiratoryRateRequest.respiratoryrate.toString())
        map["dataType"] = toRequestBody(respiratoryRateRequest.dataType.toString())

        return AppClientManager.instance.watchService.respiratoryRateUpload(map)
    }

    /*suspend fun sportUpload(sportRequest: SportRequest): WatchUploadResponse {
        val map: MutableMap<String, RequestBody?> = HashMap()
        map["sportStartTime"] = toRequestBody(sportRequest.sportStartTime)
        map["sportType"] = toRequestBody(sportRequest.sportType.toString())
        map["sportStep"] = toRequestBody(sportRequest.sportStep.toString())
        map["sportCalorie"] = toRequestBody(sportRequest.sportCalorie.toString())
        map["sportDistance"] = toRequestBody(sportRequest.sportDistance.toString())
        map["sportHR"] = toRequestBody(sportRequest.sportHR.toString())

        return AppClientManager.instance.watchservice.sportUpload(map)
    }*/

    fun toRequestBody(value: String?): RequestBody? {
        return value?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
    }

    suspend fun getGetSteps(request: WatchCommonRequest): GetStepsData {
        return try {
            AppClientManager.instance.watchService.getGetSteps(
                request.memberId,
                request.startTime,
                request.endTime
            )
        } catch (e: Exception) {
            GetStepsData()
        }
    }

    suspend fun getHeartRate(heartRateRequest: HeartRateRequest): HeartRateResponse {
        return try {
            AppClientManager.instance.watchService.getHeartRate(
                heartRateRequest.heartStartTime,
                heartRateRequest.heartEndTime,
                heartRateRequest.memberId
            )
        } catch (e: Exception) {
            HeartRateResponse()
        }
    }

    suspend fun getOxygen(oxygenRequest: OxygenRequest): OxygenResponse {
        return try {
            AppClientManager.instance.watchService.getOxygen(
                oxygenRequest.startTime,
                oxygenRequest.endTime,
                oxygenRequest.memberId
            )
        } catch (e: Exception) {
            OxygenResponse()
        }
    }

    suspend fun getSleep(sleepRequest: SleepRequest): SleepResponse {
        return try {
            AppClientManager.instance.watchService.getSleep(
                sleepRequest.startTime,
                sleepRequest.endTime,
                sleepRequest.memberId
            )
        } catch (e: Exception) {
            SleepResponse()
        }
    }

    suspend fun getSleepDetail(sleepRequest: SleepRequest): SleepDetailResponse {
        return try {
            AppClientManager.instance.watchService.getSleepDetail(
                sleepRequest.startTime,
                sleepRequest.endTime,
                sleepRequest.memberId
            )
        } catch (e: Exception) {
            SleepDetailResponse()
        }
    }

    suspend fun getBP(bpRequest: BPRequest): BPResponse {
        return try {
            AppClientManager.instance.watchService.getBP(
                bpRequest.bloodStartTime,
                bpRequest.bloodEndTime,
                bpRequest.memberId
            )
        } catch (e: Exception) {
            BPResponse()
        }
    }

    suspend fun getECG(ecgListRequest: EcgListRequest): EcgResponse {
        return try {
            AppClientManager.instance.watchService.getECG(
                ecgListRequest.startTime,
                ecgListRequest.endTime,
                ecgListRequest.memberId
            )
        } catch (e: Exception) {
            EcgResponse()
        }
    }

    suspend fun getWarrantyinfo(deviceNo: String): GetWarrantyinfoData {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.watchService.getWarrantyinfo(
                baseRequest.member_id,
                baseRequest.member_pwd,
                deviceNo
            )
        } catch (e: Exception) {
            GetWarrantyinfoData()
        }
    }

    suspend fun uploadKcal(request: KcalUploadRequest): WatchCommonResponse {
        return try {
            AppClientManager.instance.watchService.uploadKcal(
                request.memberId,
                request.startTime,
                request.KCAL,
                request.dataType
            )
        } catch (e: Exception) {
            WatchCommonResponse()
        }
    }

    suspend fun uploadBp2(request: ArmUploadRequest): WatchCommonResponse {
        return try {
            AppClientManager.instance.watchService.uploadBp2(
                request.memberId,
                request.bloodStartTime,
                request.LbloodDBP,
                request.LbloodSBP,
                request.LbloodPP,
                request.LbloodMAP,
                request.RbloodDBP,
                request.RbloodSBP,
                request.RbloodPP,
                request.RbloodMAP,
                request.heartValue,
                request.dataType
            )
        } catch (e: Exception) {
            WatchCommonResponse()
        }
    }


    suspend fun getKcal(request: WatchCommonRequest): GetKcalData {
        return try {
            AppClientManager.instance.watchService.getKcal(
                request.memberId,
                request.startTime,
                request.endTime
            )
        } catch (e: Exception) {
            GetKcalData()
        }
    }

    suspend fun getBp2(watchCommonRequest: WatchCommonRequest): GetBp2Data {
        return try {
            AppClientManager.instance.watchService.getBp2(
                watchCommonRequest.memberId,
                watchCommonRequest.startTime,
                watchCommonRequest.endTime
            )
        } catch (e: Exception) {
            GetBp2Data()
        }
    }

    suspend fun getMPOD(watchCommonRequest: WatchCommonRequest): GetMpodData {
        return try {
            AppClientManager.instance.watchService.getMPOD(
                watchCommonRequest.memberId,
                watchCommonRequest.startTime,
                watchCommonRequest.endTime
            )
        } catch (e: Exception) {
            GetMpodData()
        }
    }

    suspend fun getBMD(getBmdRequest: GetBmdRequest): GetBmdData {
        return try {
            AppClientManager.instance.watchService.getBMD(
                getBmdRequest.memberId,
                getBmdRequest.startTime,
                getBmdRequest.endTime
            )
        } catch (e: Exception) {
            GetBmdData()
        }
    }

    suspend fun getTemperature(temperatureListRequest: TemperatureListRequest): TemperatureResponse {
        return try {
            AppClientManager.instance.watchService.getTemperature(
                temperatureListRequest.startTime,
                temperatureListRequest.endTime,
                temperatureListRequest.memberId
            )
        } catch (e: Exception) {
            TemperatureResponse()
        }
    }

//    suspend fun getRespiratoryRate(respiratoryRateListRequest: RespiratoryRateListRequest): RespiratoryRateResponse {
//        return try {
//            AppClientManager.instance.watchService.getRespiratoryRate(
//                respiratoryRateListRequest.startTime,
//                respiratoryRateListRequest.endTime,
//                respiratoryRateListRequest.memberId
//            )
//        } catch (e: Exception) {
//            RespiratoryRateResponse()
//        }
//    }

    suspend fun getBreathRate(breathRateRequest: BreathRateRequest): BreathRateResponse {
        return try {
            AppClientManager.instance.watchService.getBreathRate(
                breathRateRequest.startTime,
                breathRateRequest.endTime,
                breathRateRequest.memberId
            )
        } catch (e: Exception) {
            BreathRateResponse()
        }
    }

//    suspend fun getSport(startTime:String,endTime:String,memberId:String): SportResponse{
//        return try {
//            AppClientManager.instance.watchservice.getSport(startTime, endTime, memberId)
//        }catch (e:Exception){
//            SportResponse()
//        }
//    }
}