/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.inter

import com.assignment.speedchecker.util.speedtest.protocol.http.HttpReader
import com.assignment.speedchecker.util.speedtest.protocol.http.HttpVersion
import com.assignment.speedchecker.util.speedtest.protocol.http.utils.IByteList
import java.util.*

/**
 * Interface for http request frame template
 *
 * @author Bertrand Martel
 */
interface IHttpFrame {
    /** http request version  */
    val httpVersion: HttpVersion

    /**
     * Retrieve reason phrase of http frame (empty string if not exists)
     *
     */
    val reasonPhrase: String

    /**
     * Retrieve status code of http frame (-1 if not exists)
     *
     */
    val statusCode: Int

    /**
     * http reader permitting to read and parse http frame on inputstream
     * channel
     */
    val reader: HttpReader

    /** http Host name  */
    val host: String

    /** list of http headers  */
    val headers: HashMap<String?, String?>

    /** request method  */
    val method: String

    /** request uri  */
    val uri: String

    /** queryString for http request  */
    val queryString: String

    /** request body content  */
    val body: IByteList

    /** identify the frame as a request frame  */
    val isHttpRequestFrame: Boolean

    /** identify the frame as a response frame  */
    val isHttpResponseFrame: Boolean

    /** identify if the frame has chunked body or nto  */
    val isChunked: Boolean
}