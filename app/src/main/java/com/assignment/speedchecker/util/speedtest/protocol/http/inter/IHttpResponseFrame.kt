/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.inter

import com.assignment.speedchecker.util.speedtest.protocol.http.HttpVersion
import com.assignment.speedchecker.util.speedtest.protocol.http.StatusCodeObject
import java.util.*

/**
 *
 * Http response frame interface
 *
 * @author Bertrand Martel
 */
interface IHttpResponseFrame {
    /** return code for response frame  */
    val returnCode: StatusCodeObject

    /** http version for response frame  */
    val httpVersion: HttpVersion

    /** headers for response frame  */
    val headers: HashMap<String?, String>
}