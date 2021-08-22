/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Constants for Speed Test library.
 *
 * @author Bertrand Martel
 */
object SpeedTestConst {
    /**
     * maximum size for report thread pool.
     */
    const val THREAD_POOL_REPORT_SIZE = 1

    /**
     * size of the write read buffer for downloading.
     */
    const val READ_BUFFER_SIZE = 65535

    /**
     * default size of each packet sent to upload server.
     */
    const val DEFAULT_UPLOAD_SIZE = 65535

    /**
     * default socket timeout in milliseconds.
     */
    const val DEFAULT_SOCKET_TIMEOUT = 10000

    /**
     * time to wait for task to complete when threadpool is shutdown.
     */
    const val THREADPOOL_WAIT_COMPLETION_MS = 500

    /**
     * http ok status code.
     */
    const val HTTP_OK = 200

    /**
     * max value for percent.
     */
    @kotlin.jvm.JvmField
    val PERCENT_MAX = BigDecimal("100")


    /**
     * nanosecond divider.
     */
    @kotlin.jvm.JvmField
    val NANO_DIVIDER = BigDecimal("1000000000")

    /**
     * bit multiplier value.
     */
    @kotlin.jvm.JvmField
    val BIT_MULTIPLIER = BigDecimal("8")

    /**
     * parsing error message.
     */
    const val PARSING_ERROR = "Error occurred while parsing "

    /**
     * writing socket error message.
     */
    const val SOCKET_WRITE_ERROR = "Error occurred while writing to socket"

    /**
     * default scale for BigDecimal.
     */
    const val DEFAULT_SCALE = 4

    /**
     * default rounding mode for BigDecimal.
     */
    val DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN

    /**
     * default port for http download/upload.
     */
    const val HTTP_DEFAULT_PORT = 80

    /**
     * default port for FTP download/upload.
     */
    const val FTP_DEFAULT_PORT = 21

    /**
     * default username for FTP download/upload.
     */
    const val FTP_DEFAULT_USER = "anonymous"

    /**
     * default password for FTP download/upload.
     */
    const val FTP_DEFAULT_PASSWORD = ""

    /**
     * Chunk to write at each iteration for upload file generation.
     */
    const val UPLOAD_FILE_WRITE_CHUNK = 64000

    /**
     * Temporary file name for upload file.
     */
    const val UPLOAD_TEMP_FILE_NAME = "speed-test-random"

    /**
     * Temporary file extension.
     */
    const val UPLOAD_TEMP_FILE_EXTENSION = ".tmp"

    /**
     * default setup time for download.
     */
    const val DEFAULT_DOWNLOAD_SETUP_TIME: Long = 0

    /**
     * default setup time for upload.
     */
    const val DEFAULT_UPLOAD_SETUP_TIME: Long = 0
}