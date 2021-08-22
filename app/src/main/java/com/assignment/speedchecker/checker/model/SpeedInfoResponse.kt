/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.checker.model


import com.google.gson.annotations.SerializedName

data class SpeedInfoResponse(
                          @SerializedName("message")
                          var message: String = "",
                          @SerializedName("status")
                          var status: String = "") {
    override fun toString(): String {
        return "SpeedInfoResponse(message='$message', status='$status')"
    }
}


