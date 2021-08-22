/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.splash.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import com.assignment.speedchecker.MainApplication
import com.assignment.speedchecker.R
import com.assignment.speedchecker.util.AppData
import com.assignment.speedchecker.util.AppUtil
import com.assignment.speedchecker.util.CallBackResult
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val TAG : String = "SplashActivity"
    private lateinit var navController: NavController
    private val appData : AppData by inject()
    private val appUtil : AppUtil by inject()
    private lateinit var requestLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        requestLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result1 ->
            if (result1.resultCode == RESULT_OK) {
                val credential: Credential? = result1.data?.getParcelableExtra(Credential.EXTRA_KEY)
                if(!TextUtils.isEmpty(credential?.id!!)) {
                    appData.putString(AppData.MOBILE_NUMBER, credential.id)
                    usualFlow()
                } else {
                    finish()
                }
            } else {
                finish()
            }
        }

        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)

        methodRequiresPermissions()

        fetchMobileNumber()

    }

    private fun fetchMobileNumber() {
        if(TextUtils.isEmpty(appData.getString(AppData.MOBILE_NUMBER))) {
            val tMgr = MainApplication.getContext()
                ?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    MainApplication.getContext()!!, Manifest.permission.READ_PHONE_NUMBERS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (!TextUtils.isEmpty(tMgr.line1Number)) {
                    appData.putString(AppData.MOBILE_NUMBER, tMgr.line1Number)
                }
            } else {
                finish()
            }
            if (TextUtils.isEmpty(appData.getString(AppData.MOBILE_NUMBER))) {
                appUtil.showCustomAlertWithAction(this@MainActivity, R.drawable.ic_info,
                    "Can't get mobile number from your mobile", "Ok, Fetch manually", object :
                        CallBackResult {
                        override fun onCall(result: String) {
                            val hintRequest = HintRequest.Builder()
                                .setPhoneNumberIdentifierSupported(true)
                                .build()
                            val intent = Credentials.getClient(applicationContext)
                                .getHintPickerIntent(hintRequest)
                            requestLauncher.launch(IntentSenderRequest.Builder(intent).build())
                        }
                    }).show()
            } else {
                usualFlow()
            }
        } else {
            usualFlow()
        }
    }

    private fun rationaleCallback(req: QuickPermissionsRequest) {
        appUtil.showCustomAlertWithAction(
            this,
            R.drawable.ic_info,
            req.rationaleMessage,
            getString(R.string.ok),
            object : CallBackResult {
                override fun onCall(result: String) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        ).show()
    }

    private fun permissionsPermanentlyDenied(req: QuickPermissionsRequest) {
        appUtil.showCustomAlertWithAction(
            this,
            R.drawable.ic_info,
            req.permanentlyDeniedMessage,
            getString(R.string.ok),
            object : CallBackResult {
                override fun onCall(result: String) {
                    appUtil.hideKeyboard(this@MainActivity)
                    finish()
                }
            }
        ).show()
    }

    private val quickPermissionsOption = QuickPermissionsOptions(
        handleRationale = true,
        rationaleMessage = "Internet speed checker mobile application need read phone number permission to function. Please allow those permission and continue.",
        permanentlyDeniedMessage = "Internet speed checker mobile application can't function without read phone number permission permission. Enable it from appsettings > ",
        rationaleMethod = { req -> rationaleCallback(req) },
        permanentDeniedMethod = { req -> permissionsPermanentlyDenied(req) }
    )

    private fun methodRequiresPermissions() = runWithPermissions(Manifest.permission.READ_PHONE_NUMBERS, options = quickPermissionsOption) {
//        appUtil.showToast("Access to mobile number permissions granted")
    }

    override fun onResume() {
        super.onResume()
        MainApplication.setActivity(this)
    }

    override fun onPause() {
        super.onPause()
        MainApplication.setActivity(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        MainApplication.setActivity(null)
    }

    private fun usualFlow() {
        if(navController.currentDestination?.id == R.id.splashFragment) {
            navController.navigate(R.id.action_splashFragment_to_speedInfoFragment)
        }
    }


}
