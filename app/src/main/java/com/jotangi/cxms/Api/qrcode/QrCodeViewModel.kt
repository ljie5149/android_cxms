package com.jotangi.cxms.Api.qrcode

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.jotangi.cxms.Api.book.QrCodeApiRepository

class QrCodeViewModel(var qrCodeApiRepository: QrCodeApiRepository) : ViewModel() {

    private val TAG: String = "${javaClass.simpleName}(TAG)"

    val uQrCode = MediatorLiveData<String?>()

    /**
     * 萬用qrcode
     */
    suspend fun universalQrCode(
        name: String,
        email: String?,
        success: () -> Unit,
        fail: (String) -> Unit
    ) {

        val response = qrCodeApiRepository.universalQrCode(name, email)

        if (response.code == "0x0200" && !response.QrCodeData.qrcode.isNullOrBlank()) {

            uQrCode.postValue(response.QrCodeData.qrcode)
            success()
        } else {

            fail(response.responseMessage.toString())
        }
    }
}