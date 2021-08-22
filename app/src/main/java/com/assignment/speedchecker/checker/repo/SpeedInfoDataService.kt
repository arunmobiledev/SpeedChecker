/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.checker.repo

import com.google.gson.Gson
import com.assignment.speedchecker.MainApplication
import com.assignment.speedchecker.R
import com.assignment.speedchecker.checker.model.SpeedInfoModel
import com.assignment.speedchecker.checker.model.SpeedInfoResponse
import com.assignment.speedchecker.util.AppData

class SpeedInfoDataService {

    fun saveSpeedInfo(gson : Gson, appData: AppData, speedInfoModel: SpeedInfoModel) : SpeedInfoResponse {
        try {
            appData.putString(AppData.SPEED_INFO, gson.toJson(speedInfoModel))
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return SpeedInfoResponse(MainApplication.getContext()!!.getString(R.string.speed_info_saved_successful), "success")
    }

}