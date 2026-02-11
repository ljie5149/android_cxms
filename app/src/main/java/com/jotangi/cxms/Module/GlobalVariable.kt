package com.jotangi.cxms.Module

import android.app.Application
import com.jotangi.cxms.utils.smartwatch.apiresponse.EcgData

class GlobalVariable : Application() {

    companion object {
        private lateinit var ecgData: EcgData
        fun setEcgData(ecgData: EcgData) {
            this.ecgData = ecgData
        }
        fun getEcgData(): EcgData {
            return ecgData
        }


        private var fpmPosition: Int = 0
        fun setFpmPosition(position: Int) {
            this.fpmPosition = position
        }
        fun getFpmPosition(): Int {
            return fpmPosition
        }
    }
}