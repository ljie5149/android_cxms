package com.jotangi.cxms.Api

import com.google.gson.GsonBuilder
import com.jotangi.cxms.Api.book.BookApiService
import com.jotangi.cxms.Api.book.QrApiService
import com.jotangi.cxms.BuildConfig
import com.jotangi.cxms.ui.home.WorkingDay
import com.jotangi.cxms.ui.home.WorkingDayTypeAdapter
import com.jotangi.cxms.utils.smartwatch.WatchApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppClientManager private constructor() {

    lateinit var watchService: WatchApiService
    lateinit var bookService: BookApiService
    lateinit var qrService: QrApiService

    companion object {
        val instance by lazy { AppClientManager() }
    }

    private var okHttpClient = OkHttpClient().newBuilder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(120, TimeUnit.SECONDS).build()

    fun init() {

        val gson = GsonBuilder()
            .registerTypeAdapter(WorkingDay::class.java, WorkingDayTypeAdapter())
            .create()
        val watchRetrofit = Retrofit.Builder()
            .baseUrl(ApiConstant.WATCH_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        watchService = watchRetrofit.create(WatchApiService::class.java)

        val bookRetrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BOOK_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        bookService = bookRetrofit.create(BookApiService::class.java)

        val qrRetrofit = Retrofit.Builder()
            .baseUrl(ApiConstant.QR_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        qrService = qrRetrofit.create(QrApiService::class.java)
    }
}