package com.assignment.speedchecker.util

import android.content.Context
import android.provider.Settings
import com.assignment.speedchecker.BuildConfig

class ApiConstants {
    companion object{
        const val BASE_URL = "http://192.168.1.6:8093/business/game/"
        const val CONNECTION_TIMEOUT = 30L
        const val READ_TIMEOUT = 30L
        const val WRITE_TIMEOUT = 30L

        fun getAppVersionCode() = BuildConfig.VERSION_CODE
        @JvmStatic fun getAppVersionName() = BuildConfig.VERSION_NAME
        fun getAndroidId(context: Context): String = Settings.Secure.getString(context.contentResolver,Settings.Secure.ANDROID_ID)
        fun getDeviceId() = BuildConfig.APPLICATION_ID

    }
}