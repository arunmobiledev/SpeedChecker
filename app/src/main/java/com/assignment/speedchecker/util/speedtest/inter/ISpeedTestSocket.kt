/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.inter

import com.assignment.speedchecker.util.speedtest.SpeedTestReport
import com.assignment.speedchecker.util.speedtest.RepeatWrapper
import com.assignment.speedchecker.util.speedtest.model.UploadStorageType
import com.assignment.speedchecker.util.speedtest.model.ComputationMethod
import com.assignment.speedchecker.util.speedtest.model.FtpMode
import java.math.RoundingMode

/**
 * Interface for speed test socket.
 *
 * @author Bertrand Martel
 */
interface ISpeedTestSocket {
    /**
     * Start upload process.
     *
     * @param uri           uri to fetch
     * @param fileSizeOctet size of file to upload
     */
    fun startUpload(uri: String?, fileSizeOctet: Int)

    /**
     * Start download process.
     *
     * @param uri uri to fetch to download file
     */
    fun startDownload(uri: String?)

    /**
     * Add a speed test listener to list.
     *
     * @param listener speed test listener to be added
     */
    fun addSpeedTestListener(listener: ISpeedTestListener)

    /**
     * Relive a speed listener from list.
     *
     * @param listener speed test listener to be removed
     */
    fun removeSpeedTestListener(listener: ISpeedTestListener)

    /**
     * close socket + shutdown thread pool.
     */
    fun forceStopTask()

    /**
     * get a temporary download/upload report at this moment.
     *
     * @return speed test download report
     */
    val liveReport: SpeedTestReport?

    /**
     * Close socket streams and socket object.
     */
    fun closeSocket()

    /**
     * Shutdown threadpool and wait for task completion.
     */
    fun shutdownAndWait()

    /**
     * get socket timeout in milliseconds ( 0 if no timeout not defined).
     *
     * @return mSocket timeout value (0 if not defined)
     */
    val socketTimeout: Int

    /**
     * retrieve size of each packet sent to upload server.
     *
     * @return size of each packet sent to upload server
     */
    val uploadChunkSize: Int

    /**
     * retrieve repeat wrapper object used to manage repeating Download/upload tasks.
     *
     * @return repeat wrapper object
     */
    val repeatWrapper: RepeatWrapper

    /**
     * retrieve rounding mode used for BigDecimal.
     *
     * @return rounding mode
     */
    val defaultRoundingMode: RoundingMode?

    /**
     * retrieve scale used for BigDecimal.
     *
     * @return mScale value
     */
    val defaultScale: Int
    /**
     * retrieve storage type used for uploaded data.
     *
     * @return storage type
     */
    /**
     * Set upload storage type.
     *
     * @param uploadStorageType upload storage type
     */
    var uploadStorageType: UploadStorageType

    /**
     * Get download setup time value.
     *
     * @return download setup time
     */
    val downloadSetupTime: Long

    /**
     * Get upload setup time value.
     *
     * @return upload setup time
     */
    val uploadSetupTime: Long
    /**
     * Get the computation method.
     *
     * @return computation method
     */
    /**
     * Set computation method used to calculate transfer rate.
     *
     * @param computationMethod model value
     */
    var computationMethod: ComputationMethod

    /**
     * Set proxy server for all DL/UL tasks.
     *
     * @param proxyUrl proxy URL
     * @return false if malformed
     */
    fun setProxyServer(proxyUrl: String?): Boolean

    /**
     * Get FTP mode.
     *
     * @return ftp mode
     */
    val ftpMode: FtpMode
}