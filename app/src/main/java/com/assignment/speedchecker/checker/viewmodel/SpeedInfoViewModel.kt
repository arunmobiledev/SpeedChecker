/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.checker.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.assignment.speedchecker.MainApplication
import com.assignment.speedchecker.R
import com.assignment.speedchecker.checker.model.SpeedInfoModel
import com.assignment.speedchecker.checker.repo.SpeedInfoDataService
import com.assignment.speedchecker.util.*
import com.assignment.speedchecker.util.speedtest.SpeedTestReport
import com.assignment.speedchecker.util.speedtest.SpeedTestSocket
import com.assignment.speedchecker.util.speedtest.inter.ISpeedTestListener
import com.assignment.speedchecker.util.speedtest.model.SpeedTestError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*

class SpeedInfoViewModel(private val mFirebaseInstance: FirebaseDatabase, val appUtil: AppUtil, private val speedInfoDataService: SpeedInfoDataService, application: Application, private val gson : Gson, val appData: AppData) : AndroidViewModel(application) {

    val toast : SingleLiveEvent<ToastModel>  = SingleLiveEvent()
    var completeListener : SingleLiveEvent<Pair<String, Boolean>>  = SingleLiveEvent()
    var speedInfoModel : MutableLiveData<SpeedInfoModel>  = MutableLiveData()
    private val testUploadFileSize =  5000000   // 1 Mb
    private val testDownloadFile =  "5M.iso"    // 1 Mb

    companion object {
        private const val TAG : String = "SpeedInfoViewModel"
    }

    fun updateUploadSpeedTask() {
        val startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.IO).launch {
            val speedTestSocket = SpeedTestSocket()
            speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {
                override fun onCompletion(report: SpeedTestReport) {
                    val transferTimeInSecs = (System.currentTimeMillis() - startTime) / 1000
                    val averageTransferRate = report.totalPacketSize / transferTimeInSecs
                    val averageTransferRateInHumanReadable = appUtil.humanReadableByteCountSI(averageTransferRate * 8)
//                    Log.d(TAG, "[COMPLETED] rate in octet/s : " + report.transferRateOctet)
//                    Log.d(TAG, "[COMPLETED] transferTimeInSecs  : $transferTimeInSecs")
                    Log.d(TAG,"[COMPLETED] averageTransferRate  : $averageTransferRateInHumanReadable")
                    Handler(Looper.getMainLooper()).post {
                        completeListener.value = Pair("upload", true)
                        val model = speedInfoModel.value as SpeedInfoModel
                        model.uploadSpeed = averageTransferRateInHumanReadable
                        speedInfoModel.value = model
                    }
                }

                override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                    Log.d(TAG, "[Error] onError : $errorMessage%")
                    Handler(Looper.getMainLooper()).post {
                        completeListener.value = Pair("upload", false)
                    }
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
//                    Log.d(TAG, "[PROGRESS] progress : $percent%")
//                    Log.d(TAG, "[PROGRESS] rate in octet/s : " + report.transferRateOctet)
                    val model = speedInfoModel.value as SpeedInfoModel
                    model.uploadSpeed =
                        appUtil.humanReadableByteCountSI(report.transferRateOctet.toLong())
                    Handler(Looper.getMainLooper()).post {
                        speedInfoModel.value = model
                    }
                }
            })

            speedTestSocket.startUpload("http://ipv4.ikoula.testdebit.info/", testUploadFileSize)
        }
    }

    fun updateDownloadSpeedTask() {
        val startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.IO).launch {
            val speedTestSocket = SpeedTestSocket()
            speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {
                override fun onCompletion(report: SpeedTestReport) {
                    val transferTimeInSecs = (System.currentTimeMillis() - startTime) / 1000
                    val averageTransferRate = report.totalPacketSize / transferTimeInSecs
                    val averageTransferRateInHumanReadable =
                        appUtil.humanReadableByteCountSI(averageTransferRate * 8)
//                    Log.d(TAG, "[COMPLETED] rate in octet/s : " + report.transferRateOctet)
//                    Log.d(TAG, "[COMPLETED] transferTimeInSecs  : $transferTimeInSecs")
                    Log.d(TAG,"[COMPLETED] averageTransferRate  : $averageTransferRateInHumanReadable")
                    Handler(Looper.getMainLooper()).post {
                        completeListener.value = Pair("download", true)
                        val model = speedInfoModel.value as SpeedInfoModel
                        model.downloadSpeed = averageTransferRateInHumanReadable
                        speedInfoModel.value = model
                    }
                }

                override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                    Log.d(TAG, "[Error] onError : $errorMessage%")
                    Handler(Looper.getMainLooper()).post {
                        completeListener.value = Pair("download", false)
                    }
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
//                    Log.d(TAG, "[PROGRESS] progress : $percent%")
//                    Log.d(TAG, "[PROGRESS] rate in octet/s : " + report.transferRateOctet)
                    val model = speedInfoModel.value as SpeedInfoModel
                    model.downloadSpeed =
                        appUtil.humanReadableByteCountSI(report.transferRateOctet.toLong())
                    Handler(Looper.getMainLooper()).post {
                        speedInfoModel.value = model
                    }
                }
            })
            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/$testDownloadFile")
        }
    }


    fun updateUser(currentSpeedInfoModel : SpeedInfoModel) {
        // get reference to 'users' node
        val mFirebaseDatabase = mFirebaseInstance.getReference("users")

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue(getApplication<MainApplication>().getString(R.string.app_name))

        if (!TextUtils.isEmpty(currentSpeedInfoModel.mobileNumber)) {
            mFirebaseDatabase.child(currentSpeedInfoModel.mobileNumber!!).setValue(currentSpeedInfoModel)
            mFirebaseDatabase.child(currentSpeedInfoModel.mobileNumber!!).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val speedInfoModel: SpeedInfoModel? = dataSnapshot.getValue(SpeedInfoModel::class.java)
                    if (speedInfoModel == null) {
                        Log.e(TAG, "User data is null!")
                        return
                    }
                    Log.e(TAG, "User data is changed!" + speedInfoModel.mobileNumber.toString() + ", " + speedInfoModel.timeStamp)
                    toast.value = ToastModel(R.drawable.ic_info, "User data updated successfully")
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read SpeedInfoModel", error.toException())
                }
            })
        }
    }

}