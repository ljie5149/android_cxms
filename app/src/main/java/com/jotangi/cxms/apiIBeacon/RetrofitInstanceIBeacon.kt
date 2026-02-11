package com.ptv.ibeacon.receiver.apiIBeacon
import com.jotangi.cxms.CONSTANT
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitInstanceIBeacon {

    val api: ApiServiceIBeacon by lazy {
        Retrofit.Builder()
            .baseUrl(CONSTANT.BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict()) // Use createNonStrict() to avoid issues
            .build()
            .create(ApiServiceIBeacon::class.java)
    }
}