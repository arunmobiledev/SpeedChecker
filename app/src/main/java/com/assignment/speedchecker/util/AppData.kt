/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.assignment.speedchecker.MainApplication

class AppData {


    companion object {
        private const val encryption = true
        const val SPEED_INFO = "SPEED_INFO"
        const val MOBILE_NUMBER = "MOBILE_NUMBER"
    }

    fun putString(key : String, data : String) {
        if(!encryption) {
            val sharedPreferences = MainApplication.getContext()?.getSharedPreferences("OpenPreferences", Context.MODE_PRIVATE)
            sharedPreferences!!.edit().putString(key, data).apply()
        } else {
            val sharedPreferences = MainApplication.getContext()?.let { provideSharedPref(it) }
            sharedPreferences!!.edit().putString(key, data).apply()
        }
    }

    fun getString(key : String) : String {
        return if(!encryption) {
            val sharedPreferences = MainApplication.getContext()?.getSharedPreferences("OpenPreferences", Context.MODE_PRIVATE)
            val value = sharedPreferences!!.getString(key, "")
            value!!
        } else {
            val sharedPreferences = MainApplication.getContext()?.let { provideSharedPref(it) }
            val value = sharedPreferences!!.getString(key, "")
            value!!
        }
    }

    fun deleteAppData() {
        if(!encryption) {
            val sharedPreferences = MainApplication.getContext()?.getSharedPreferences("OpenPreferences", Context.MODE_PRIVATE)
            sharedPreferences!!.edit().clear().apply()
        } else {
            val sharedPreferences = MainApplication.getContext()?.let { provideSharedPref(it) }
            sharedPreferences!!.edit().clear().apply()
        }
    }

    private fun provideSharedPref(applicationContext : Context) : SharedPreferences {

        // Step 1: Create or retrieve the Master Key for encryption/decryption
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .build()

        val masterKey = MasterKey.Builder(applicationContext)
            .setKeyGenParameterSpec(spec)
            .build()

        // Step 2: Initialize/open an instance of EncryptedSharedPreferences
        return EncryptedSharedPreferences.create(
            applicationContext,
            "AppData",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

}


