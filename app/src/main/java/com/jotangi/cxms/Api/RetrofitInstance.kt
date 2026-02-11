package com.ptv.ibeacon.receiver.api
import com.jotangi.cxms.CONSTANT
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitInstance {

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(CONSTANT.BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict()) // Use createNonStrict() to avoid issues
            .build()
            .create(ApiService::class.java)
    }
}