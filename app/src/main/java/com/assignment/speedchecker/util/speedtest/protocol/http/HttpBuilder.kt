/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http

import kotlin.Throws
import com.assignment.speedchecker.util.speedtest.protocol.http.utils.IByteList
import com.assignment.speedchecker.util.speedtest.protocol.http.constants.HttpHeader
import java.io.UnsupportedEncodingException
import java.util.HashMap

/**
 * Http request builder
 *
 * @author Bertrand Martel
 */
object HttpBuilder {
    /**
     * Send an httpRequest with content length dynamic
     *
     * @param method
     * request method
     * @param uri
     * request uri
     * @param content
     * request body content
     * @param headers
     * hashmap containing all http headers for this request
     * (content-length will be added if not here and only if body
     * length not 0)
     * @return request in String format
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    fun httpRequest(
        method: String, uri: String, content: IByteList,
        headers: HashMap<String?, String?>
    ): String {
        val version = HttpVersion(1, 1)
        if (!headers.containsKey(HttpHeader.CONTENT_LENGTH)
            && content.bytes.size != 0
        ) {
            headers[HttpHeader.CONTENT_LENGTH] = content.bytes.size.toString()
        }
        val request = HttpFrame(
            method, version,
            headers, uri, content
        )
        return request.toString()
    }
}