package com.jotangi.cxms

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.jotangi.cxms.Api.AppClientManager
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig



class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Initialize Firebase Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        // Initialize Firebase Remote Config
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

        // Set default Remote Config parameters (optional)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        SharedPreferencesUtil.instances.init(this)
        WatchUtils.instance.init(this)
        WatchUtils.instance.initWatchSDK()
        WatchUtils.instance.deviceToApp()
        AppClientManager.instance.init()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
            modules(appModule2)
            modules(appModule3)
        }
    }
}