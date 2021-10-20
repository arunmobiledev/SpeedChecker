package com.assignment.speedchecker.util

import com.google.gson.Gson

class ApiUtils {

}

enum class ApiStatus { LOADING , ERROR, SUCCESS}

class ApiError {
    private var status = ""
    private var message: String = ""


    constructor(error: String) {
        val apiError : ApiError = Gson().fromJson(error, ApiError::class.java)
        status =  apiError.status
        message =  apiError.message
    }

    fun status(): String {
        return status
    }

    fun message(): String {
        return message
    }
}