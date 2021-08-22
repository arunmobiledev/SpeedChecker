/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http

import kotlin.Throws
import com.assignment.speedchecker.util.speedtest.protocol.http.states.HttpStates
import com.assignment.speedchecker.util.speedtest.protocol.http.utils.IByteList
import com.assignment.speedchecker.util.speedtest.protocol.http.utils.ListOfBytes
import com.assignment.speedchecker.util.speedtest.protocol.http.inter.IHttpFrame
import com.assignment.speedchecker.util.speedtest.protocol.http.constants.HttpConstants
import com.assignment.speedchecker.util.speedtest.protocol.http.constants.HttpHeader
import com.assignment.speedchecker.util.speedtest.protocol.http.constants.HttpMethod
import com.assignment.speedchecker.util.speedtest.protocol.socket.DataBufferConst
import com.assignment.speedchecker.util.speedtest.protocol.http.utils.StringUtils
import java.io.*
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import java.nio.charset.Charset
import java.util.*

/**
 * Http request frame builder and parser
 *
 * @author Bertrand Martel
 */
class HttpFrame : IHttpFrame {
    /** http request version  */
    override var httpVersion = HttpVersion(1, 1)
        private set

    /** identify frame as a response frame  */
    override var isHttpResponseFrame = false
        private set

    /** identify frame as a request frame  */
    override var isHttpRequestFrame = false
        private set

    /**
     * idnetify if http frame is chunked
     */
    override var isChunked = false
        private set

    /**
     * http reader object used to extract http request/response frame from
     * inputstream
     */
    override var reader = HttpReader()
        private set

    /** http Host name  */
    override var host = ""
        private set
    /**
     * Setter for headers
     */
    /** list of http headers  */
    override var headers = HashMap<String?, String?>()

    /** request method  */
    override var method = ""
        private set

    /** request uri  */
    override var uri = ""
        private set

    /** queryString for http request  */
    override val queryString = ""

    /** request body content  */
    override var body: IByteList = ListOfBytes()
        private set

    /** http frame status code (-1 if not exists)  */
    override var statusCode = -1
        private set

    /** http frame reason phrase (empty string if not exists)  */
    override var reasonPhrase = ""
        private set

    /** Default constructor used in parse  */
    constructor() {
        reader = HttpReader()
    }

    /**
     * Http builder with all parameters (RFC)
     *
     * @param method
     * request method
     * @param httpVersion
     * http version
     * @param headers
     * request http headers
     * @param uri
     * request uri
     * @param body
     * request body content
     */
    constructor(
        method: String,
        httpVersion: HttpVersion?,
        headers: HashMap<String?, String?>,
        uri: String,
        body: IByteList
    ) {
        this.httpVersion = HttpVersion(1, 1)
        this.headers = headers
        this.method = method
        this.uri = uri
        this.body = body
    }

    /**
     * Format request to string to be sent to outputStream (be careful NOT to
     * insert space between header name and ":")
     */
    override fun toString(): String {
        var ret = ""
        ret = if (method != "") {
            method + " " + uri + " " + httpVersion.toString() + HttpConstants.HEADER_DELEMITER
        } else {
            uri + " " + httpVersion.toString() + HttpConstants.HEADER_DELEMITER
        }
        if (!headers.containsKey(HttpHeader.CONTENT_LENGTH) && body.size > 0) {
            headers[HttpHeader.CONTENT_LENGTH] = String(body.bytes).length.toString()
        }
        val cles: Set<String?> = headers.keys
        val it = cles.iterator()
        while (it.hasNext()) {
            val cle: Any? = it.next()
            val valeur: Any? = headers[cle]
            ret += cle.toString() + HttpConstants.HEADER_VALUE_DELIMITER + " " + valeur.toString() + HttpConstants.HEADER_DELEMITER
        }
        ret += HttpConstants.HEADER_DELEMITER
        try {
            ret += String(body.bytes, charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        ret += HttpConstants.HEADER_DELEMITER
        return ret
    }

    private fun cleanParser() {
        httpVersion = HttpVersion(1, 1)
        headers = HashMap()
        method = ""
        uri = ""
        body = ListOfBytes()
        statusCode = -1
        reasonPhrase = ""
        isHttpRequestFrame = false
        isHttpResponseFrame = false
    }

    @Throws(IOException::class, InterruptedException::class)
    fun parseHttp(`in`: InputStream): HttpStates {
        return try {
            var errorCode = HttpStates.HTTP_STATE_NONE
            cleanParser()
            synchronized(`in`) {
                errorCode = decodeFrame(`in`)
                if (errorCode == HttpStates.HTTP_FRAME_OK) {
                    /* parse header */
                    val headerError = parseHeader(`in`)
                    if (headerError == HttpStates.HTTP_FRAME_OK && headers.containsKey(HttpHeader.TRANSFER_ENCODING.toLowerCase())
                        && headers[HttpHeader.TRANSFER_ENCODING.toLowerCase()].toString() == "chunked"
                    ) {
                        isChunked = true
                        val br = BufferedReader(InputStreamReader(`in`))
                        var t: String
                        var ret = ""
                        var init = 0
                        var numberOfChar = 0
                        var text = false
                        while (br.readLine().also { t = it } != null) {
                            text = true
                            if (init == 0 || numberOfChar == 0) {
                                numberOfChar = try {
                                    t.toUpperCase().toLong(16).toInt()
                                } catch (e: NumberFormatException) {
                                    break
                                }
                                if (numberOfChar == 0) {
                                    break
                                }
                                text = false
                                if (init == 0) {
                                    init = 1
                                }
                            }
                            if (text == true) {
                                if (t.trim { it <= ' ' } == "0") {
                                    numberOfChar = 0
                                } else {
                                    ret += t
                                    numberOfChar = numberOfChar - t.length
                                }
                            }
                        }
                        br.close()
                        body = ListOfBytes(ret)
                    } else if (headerError == HttpStates.HTTP_FRAME_OK) {
                        isChunked = false
                        /* parse body request */return parseBody(`in`)
                    }
                    return headerError
                }
            }
            // displayInformation();
            errorCode
        } catch (e: SocketTimeoutException) {
            HttpStates.SOCKET_ERROR
        }
    }

    /**
     * Method used to parse inputstream data extracted from socket in order to
     * retrieve method / uri / host and request body
     *
     * @param inputstream
     * socket inputStream
     * @return null if socket connection failed or bad request frame and String
     * terminating by '\r\n' if successfully parsed
     * @throws IOException
     * @throws InterruptedException
     */
    @Throws(IOException::class, InterruptedException::class)
    fun decodeFrame(inputstream: InputStream?): HttpStates {
        var httpFrame: String? = null

        /* read data from inputstream (until no data left on inputstream) */
        /* if data is not present return exception EOF */httpFrame = reader.readLine(inputstream)
        if (httpFrame == null) {
            return HttpStates.HTTP_READING_ERROR
        }
        if (!httpFrame.contains("HTTP")) {
            return HttpStates.MALFORMED_HTTP_FRAME
        }

        /* identify key with delimeter space " " */
        val st = StringTokenizer(httpFrame, " ")
        val firstToken = st.nextToken()
        if (firstToken == HttpMethod.POST_REQUEST || firstToken == HttpMethod.GET_REQUEST || firstToken == HttpMethod.OPTIONS_REQUEST || firstToken == HttpMethod.DELETE_REQUEST || firstToken == HttpMethod.PUT_REQUEST) {
            method = firstToken
            if (st.hasMoreTokens()) uri = st.nextToken() else return HttpStates.MALFORMED_HTTP_FRAME
            if (!st.hasMoreTokens()) return HttpStates.MALFORMED_HTTP_FRAME
            val httpVersion = st.nextToken()
            if (httpVersion.startsWith("HTTP/")) this.httpVersion =
                HttpVersion(httpVersion) else return HttpStates.HTTP_WRONG_VERSION
            isHttpRequestFrame = true
        } else if (firstToken.startsWith("HTTP/")) {
            httpVersion = HttpVersion(firstToken)
            if (httpVersion.versionDigit1 == 0) {
                return HttpStates.HTTP_WRONG_VERSION
            }
            if (!st.hasMoreTokens()) return HttpStates.MALFORMED_HTTP_FRAME
            val statusCodeTemp = st.nextToken()
            if (StringUtils.isInteger(statusCodeTemp)) {
                statusCode = statusCodeTemp.toInt()
            } else {
                return HttpStates.MALFORMED_HTTP_FRAME
            }
            if (st.hasMoreTokens()) reasonPhrase =
                st.nextToken() else return HttpStates.MALFORMED_HTTP_FRAME
            isHttpResponseFrame = true
        } else {
            return HttpStates.MALFORMED_HTTP_FRAME
        }
        return HttpStates.HTTP_FRAME_OK
    }

    /**
     * Parse header putting all headers keys and values found into a map object
     *
     * @param inputstream
     * socket inputstream
     * @throws IOException
     */
    @Throws(IOException::class)
    fun parseHeader(inputstream: InputStream?): HttpStates {
        var s = reader.readLine(inputstream)
        while (s != null && s.length != 0) {

            /* identify separator ":" of fields */
            val index = s.indexOf(":")
            if (index > 0) {

                /* extract the key (to the left of ":" separator) */
                val header = s.substring(0, index).trim { it <= ' ' }
                /* extract the value (to the right of ":" separator) */
                val value = s.substring(index + 1).trim { it <= ' ' }
                /* key must be set in lower case */
                val key = header.toLowerCase()
                headers[key] = value
            }
            s = reader.readLine(inputstream)
        }
        /* get host headers and set object value */if (headers.containsKey(HttpHeader.HOST.toLowerCase())) {
            host = headers[HttpHeader.HOST.toLowerCase()].toString()
        }
        return HttpStates.HTTP_FRAME_OK
    }

    /**
     * parse request body only in order to set value of requestBody String
     *
     * @param inputstream
     * inputstream used to read data from socket object
     * @throws IOException
     */
    @Throws(IOException::class)
    fun parseBody(inputstream: InputStream): HttpStates {

        /* identify content length */
        val length = contentLength
        if (length > 0) {
            val numberOfBlockToWrite = length % DataBufferConst.DATA_BLOCK_SIZE_LIMIT
            /* define number of block to write */
            var numberOfBlock = 0
            numberOfBlock = if (numberOfBlockToWrite == 0) {
                length / DataBufferConst.DATA_BLOCK_SIZE_LIMIT
            } else {
                length / DataBufferConst.DATA_BLOCK_SIZE_LIMIT + 1
            }
            val list = ListOfBytes()
            for (i in 0 until numberOfBlock) {
                if (numberOfBlockToWrite != 0 && i == numberOfBlock - 1) {
                    /* this is the last block to write */
                    val size = length - i * DataBufferConst.DATA_BLOCK_SIZE_LIMIT
                    val data = ByteArray(size)
                    for (j in data.indices) {
                        val byteToBeRead = inputstream.read().toByte()
                        data[j] = byteToBeRead
                    }
                    list.add(data)
                } else {
                    /* this is not the last block to write */
                    val data = ByteArray(DataBufferConst.DATA_BLOCK_SIZE_LIMIT)
                    for (j in data.indices) {
                        val byteToBeRead = inputstream.read().toByte()
                        data[j] = byteToBeRead
                    }
                    list.add(data)
                }
            }
            if (inputstream.available() > 0) {
                if (inputstream.read() == '\r'.toInt()) {
                    if (inputstream.read() != '\n'.toInt()) {
                        return HttpStates.HTTP_BODY_PARSE_ERROR
                    }
                } else {
                    return HttpStates.HTTP_BODY_PARSE_ERROR
                }
            }
            body = list
        } else {
            body = ListOfBytes()
        }
        return HttpStates.HTTP_FRAME_OK
    }

    /**
     * get Content-Length value
     *
     * @return http content length
     */
    val contentLength: Int
        get() = if (headers.containsKey(HttpHeader.CONTENT_LENGTH.toLowerCase())) {
            try {
                headers[HttpHeader.CONTENT_LENGTH.toLowerCase()]!!.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        } else {
            0
        }
}