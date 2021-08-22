/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http

import com.assignment.speedchecker.util.speedtest.protocol.http.constants.HttpConstants
import com.assignment.speedchecker.util.speedtest.protocol.http.constants.HttpHeader
import com.assignment.speedchecker.util.speedtest.protocol.http.inter.IHttpResponseFrame
import java.io.UnsupportedEncodingException
import java.util.HashMap

/**
 * HTTP response frame builder/parser
 *
 * @author Bertrand Martel
 */
class HttpResponseFrame(
    /** return code for response frame  */
    override val returnCode: StatusCodeObject,
    /** http version for response frame  */
    override val httpVersion: HttpVersion, headers: HashMap<String?, String>,
    body: ByteArray
) : IHttpResponseFrame {

    /** headers for response frame  */
    override var headers = HashMap<String?, String>()

    /** body for http response frame  */
    var body = byteArrayOf()

    /**
     * Format request to string to be sent to outputStream (be careful NOT to
     * insert space between header name and ":")
     */
    override fun toString(): String {
        var ret = (httpVersion.toString() + " "
                + returnCode.toString() + HttpConstants.HEADER_DELEMITER)
        if (!headers.containsKey(HttpHeader.CONTENT_LENGTH)
            && body.size > 0
        ) {
            headers[HttpHeader.CONTENT_LENGTH] = body.size.toString()
        }
        val cles: Set<String?> = headers.keys
        val it = cles.iterator()
        while (it.hasNext()) {
            val cle: Any? = it.next()
            val valeur: Any? = headers[cle]
            ret += (cle.toString() + HttpConstants.HEADER_VALUE_DELIMITER + " "
                    + valeur.toString() + HttpConstants.HEADER_DELEMITER)
        }
        if (body.size > 0) {
            ret += HttpConstants.HEADER_DELEMITER
            try {
                ret += String(body, charset("UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            ret += HttpConstants.HEADER_DELEMITER
        } else {
            ret += HttpConstants.HEADER_DELEMITER
        }
        return ret
    }

    /**
     * Builder for http response frame
     *
     * @param returnCode
     * status return code
     * @param httpVersion
     * http version
     * @param headers
     * hashmap of all headers
     */
    init {
        this.headers = headers
        this.body = body
    }
}