package com.jotangi.cxms.Api.book

import com.jotangi.cxms.Api.AppClientManager
import com.jotangi.cxms.Api.book.apirequest.SetGuestQrCodeRequest
import com.jotangi.cxms.Api.book.apiresponse.GetQrCodeResponse
import com.jotangi.cxms.Api.book.apiresponse.IsSmartDoctorStaff
import com.jotangi.cxms.Api.book.apiresponse.QrDataResponse
import com.jotangi.cxms.jackyVariant.Common
import com.jotangi.cxms.ui.mylittlemin.BaseBookRequest

class QrCodeApiRepository {

    /**
     * 1. 取得app會員是否是智醫城員工
     */
    suspend fun isSmartDoctorStaff(): IsSmartDoctorStaff {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.qrService.isSmartDoctorStaff(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
            )
        } catch (e: Exception) {
            IsSmartDoctorStaff()
        }
    }

    /**
     * 2. App 訪客設定qrcode資訊
     * (注意:此api 只給app 訪客使用, web 訪客以及app員工不可使用)
     */
    suspend fun setGuestQrCode(request: SetGuestQrCodeRequest): QrDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.qrService.setGuestQrCode(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                request.name,
                request.email,
                request.startTime,
                request.endTime
            )
        } catch (e: Exception) {
            QrDataResponse()
        }
    }

    /**
     * 3. App 病患設定qrcode資訊
     * (注意:此api 只給app 病患使用, app 訪客以及app員工不可使用)
     */
    suspend fun setPatientQrCode(request: SetGuestQrCodeRequest): QrDataResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.qrService.setPatientQrCode(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                request.name,
                request.email,
                request.startTime,
            )
        } catch (e: Exception) {
            QrDataResponse()
        }
    }

    /**
     * 5. 取得qrcode資訊
     * (注意:app訪客, 員工, 病患皆可使用此取得)
     */
    suspend fun getQrCode(type: String): GetQrCodeResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.qrService.getQrCode(
                Common.getToken(),
                baseRequest.member_id,
                baseRequest.member_pwd,
                type
            )
        } catch (e: Exception) {
            GetQrCodeResponse()
        }
    }

    /**
     * 萬用qrcode
     */
    suspend fun universalQrCode(name: String, email: String?): GetQrCodeResponse {
        return try {
            val baseRequest = BaseBookRequest()
            AppClientManager.instance.qrService.universalQrCode(
                Common.getToken(),
                baseRequest.member_id,
                name,
                email,
                baseRequest.member_pid
            )
        } catch (e: Exception) {
            GetQrCodeResponse()
        }
    }
}