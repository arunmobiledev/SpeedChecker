/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.checker.model

data class SpeedInfoModel(var timeStamp : Long, var mobileNumber : String?, var downloadSpeed : String?, var uploadSpeed : String?)  {

    constructor() : this("") {

    }

    constructor(mobileNumber: String?) : this(System.currentTimeMillis(), mobileNumber,"0 Kb","0 Kb") {
        updateTime()
    }

    fun isValidMobileNumber(s : String) : Boolean {
        return s.length in 6..15
    }

    fun updateTime() {
        timeStamp = System.currentTimeMillis()
    }

}