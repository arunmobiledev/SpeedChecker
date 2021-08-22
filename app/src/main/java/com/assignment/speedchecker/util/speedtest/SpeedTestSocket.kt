/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest

import com.assignment.speedchecker.util.speedtest.inter.ISpeedTestListener
import com.assignment.speedchecker.util.speedtest.model.UploadStorageType
import com.assignment.speedchecker.util.speedtest.model.ComputationMethod
import com.assignment.speedchecker.util.speedtest.model.FtpMode
import com.assignment.speedchecker.util.speedtest.inter.ISpeedTestSocket
import com.assignment.speedchecker.util.speedtest.model.SpeedTestMode
import com.assignment.speedchecker.util.speedtest.inter.IRepeatListener
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * Client socket main implementation.
 *
 *
 * Two modes upload and download
 *
 *
 * upload will write a file to a specific host with given uri. The file is
 * randomly generated with a given size
 *
 *
 * download will retrieve a content from a specific host with given uri.
 *
 *
 * For both mode, transfer rate is calculated independently from mSocket initial
 * connection
 *
 * @author Bertrand Martel
 */
class SpeedTestSocket : ISpeedTestSocket {
    /**
     * Retrieve scale used for BigDecimal.
     *
     * @return mScale value
     */
    /**
     * Set the default scale for BigDecimal.
     *
     * @param scale mScale value
     */
    /**
     * BigDecimal scale used in transfer rate calculation.
     */
    override var defaultScale = SpeedTestConst.DEFAULT_SCALE
    /**
     * Retrieve rounding mode used for BigDecimal.
     *
     * @return rounding mode
     */
    /**
     * Set the default RoundingMode for BigDecimal.
     *
     * @param roundingMode rounding mode.
     */
    /**
     * BigDecimal RoundingMode used in transfer rate calculation.
     */
    override var defaultRoundingMode = SpeedTestConst.DEFAULT_ROUNDING_MODE
    /**
     * Get FTP mode.
     */
    /**
     * Set FTP mode passive or active.
     *
     * @param mode ftp mode.
     */
    /**
     * FTP mode passive or active.
     */
    override var ftpMode = FtpMode.PASSIVE
    /**
     * Retrieve upload storage type (RAM or ROM).
     *
     * @return upload storage type
     */
    /**
     * Set upload storage type.
     *
     * @param uploadStorageType upload storage type
     */
    /**
     * Upload storage type.
     */
    override var uploadStorageType = UploadStorageType.RAM_STORAGE

    /**
     * speed test listener list.
     */
    private val mListenerList: MutableList<ISpeedTestListener> = ArrayList()
    /**
     * retrieve size of each packet sent to upload server.
     *
     * @return size of each packet sent to upload server
     */
    /**
     * set size of each packet sent to upload server.
     *
     * @param uploadChunkSize new size of each packet sent to upload server
     */
    /**
     * this is the size of each data sent to upload server.
     */
    override var uploadChunkSize = SpeedTestConst.DEFAULT_UPLOAD_SIZE

    /**
     * mSocket timeout.
     */
    private var mSocketTimeout = SpeedTestConst.DEFAULT_SOCKET_TIMEOUT

    /**
     * Speed test repeat wrapper.
     */
    override val repeatWrapper = RepeatWrapper(this)

    /**
     * Speed tets task object used to manage download/upload operations.
     */
    private val mTask = SpeedTestTask(this, mListenerList)
    /**
     * Get download setup time value.
     *
     * @return download setup time value
     */
    /**
     * Set the setup time for download.
     *
     * @param setupTime point in time from which download speed rate should be computed
     */
    /**
     * setup time for calculating the threshold before updating the calculation of download.
     */
    override var downloadSetupTime = SpeedTestConst.DEFAULT_DOWNLOAD_SETUP_TIME
    /**
     * Get upload setup time value.
     *
     * @return upload setup time value
     */
    /**
     * Set the setup time for upload.
     *
     * @param setupTime point in time from which upload speed rate should be computed
     */
    /**
     * setup time for calculating the threshold before updating the calculation of upload.
     */
    override var uploadSetupTime = SpeedTestConst.DEFAULT_UPLOAD_SETUP_TIME

    /**
     * report interval in milliseconds.
     */
    private var mReportInterval = -1
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
    /**
     * Computation method used to calculate transfer rate.
     */
    override var computationMethod = ComputationMethod.MEDIAN_ALL_TIME

    constructor() {}

    /**
     * Initialize global report interval value.
     *
     * @param reportInterval report value in milliseconds
     */
    constructor(reportInterval: Int) {
        mReportInterval = reportInterval
    }

    /**
     * initialize report task.
     *
     * @param reportInterval report interval in milliseconds
     */
    private fun initReportTask(reportInterval: Int) {
        mTask.renewReportThreadPool()
        mTask.reportThreadPool!!.scheduleAtFixedRate({
            val report = liveReport
            for (listener in mListenerList) {
                listener.onProgress(report!!.progressPercent, report)
            }
        }, reportInterval.toLong(), reportInterval.toLong(), TimeUnit.MILLISECONDS)
    }

    /**
     * Add a speed test listener to list.
     *
     * @param listener speed test listener to be added
     */
    override fun addSpeedTestListener(listener: ISpeedTestListener) {
        mListenerList.add(listener)
    }

    /**
     * Relive a speed listener from list.
     *
     * @param listener speed test listener to be removed
     */
    override fun removeSpeedTestListener(listener: ISpeedTestListener) {
        mListenerList.remove(listener)
    }

    /**
     * Shutdown threadpool and wait for task completion.
     */
    override fun shutdownAndWait() {
        mTask.shutdownAndWait()
    }

    /**
     * Start download process with a fixed duration.
     *
     * @param uri         uri to fetch to download file
     * @param maxDuration maximum duration of the speed test in milliseconds
     */
    fun startFixedDownload(
        uri: String?,
        maxDuration: Int
    ) {
        if (mReportInterval != -1 && !mTask.isReportInterval) {
            initReportTask(mReportInterval)
            mTask.isReportInterval = true
        }
        mTask.renewReportThreadPool()
        mTask.reportThreadPool!!.schedule(
            { forceStopTask() },
            maxDuration.toLong(),
            TimeUnit.MILLISECONDS
        )
        startDownload(uri)
    }

    /**
     * Start download process with a fixed duration.
     *
     * @param uri            uri to fetch to download file
     * @param maxDuration    maximum duration of the speed test in milliseconds
     * @param reportInterval report interval in milliseconds
     */
    fun startFixedDownload(
        uri: String?,
        maxDuration: Int,
        reportInterval: Int
    ) {
        initReportTask(reportInterval)
        mTask.isReportInterval = true
        startFixedDownload(uri, maxDuration)
    }

    /**
     * Start download process.
     *
     * @param uri            uri to fetch to download file
     * @param reportInterval report interval in milliseconds
     */
    fun startDownload(
        uri: String?,
        reportInterval: Int
    ) {
        initReportTask(reportInterval)
        mTask.isReportInterval = true
        startDownload(uri)
    }

    /**
     * Start download process.
     *
     * @param uri uri to fetch to download file
     */
    override fun startDownload(uri: String?) {
        if (mReportInterval != -1 && !mTask.isReportInterval) {
            initReportTask(mReportInterval)
            mTask.isReportInterval = true
        }
        mTask.startDownloadRequest(uri)
    }

    /**
     * Set proxy server for all DL/UL tasks.
     *
     * @param proxyUrl proxy URL
     * @return false if malformed
     */
    override fun setProxyServer(proxyUrl: String?): Boolean {
        return mTask.setProxy(proxyUrl)
    }

    /**
     * Start upload process.
     *
     * @param uri           uri to fetch
     * @param fileSizeOctet size of file to upload
     * @param maxDuration   maximum duration of speed test in milliseconds
     */
    fun startFixedUpload(
        uri: String?,
        fileSizeOctet: Int,
        maxDuration: Int
    ) {
        if (mReportInterval != -1 && !mTask.isReportInterval) {
            initReportTask(mReportInterval)
            mTask.isReportInterval = true
        }
        mTask.renewReportThreadPool()
        mTask.reportThreadPool!!.schedule(
            { forceStopTask() },
            maxDuration.toLong(),
            TimeUnit.MILLISECONDS
        )
        startUpload(uri, fileSizeOctet)
    }

    /**
     * Start upload process.
     *
     * @param uri            uri to fetch
     * @param fileSizeOctet  size of file to upload
     * @param maxDuration    maximum duration of speed test in milliseconds
     * @param reportInterval report interval in milliseconds
     */
    fun startFixedUpload(
        uri: String?,
        fileSizeOctet: Int,
        maxDuration: Int,
        reportInterval: Int
    ) {
        initReportTask(reportInterval)
        mTask.isReportInterval = true
        startFixedUpload(uri, fileSizeOctet, maxDuration)
    }

    /**
     * Start upload process.
     *
     * @param uri            uri to fetch
     * @param fileSizeOctet  size of file to upload
     * @param reportInterval report interval in milliseconds
     */
    fun startUpload(
        uri: String?,
        fileSizeOctet: Int,
        reportInterval: Int
    ) {
        initReportTask(reportInterval)
        mTask.isReportInterval = true
        startUpload(uri, fileSizeOctet)
    }

    /**
     * Start upload process.
     *
     * @param uri           uri to fetch
     * @param fileSizeOctet size of file to upload
     */
    override fun startUpload(uri: String?, fileSizeOctet: Int) {
        if (mReportInterval != -1 && !mTask.isReportInterval) {
            initReportTask(mReportInterval)
            mTask.isReportInterval = true
        }
        mTask.startUploadRequest(uri, fileSizeOctet)
    }

    /**
     * Start repeat download task.
     *
     * @param uri            uri to fetch to download file
     * @param repeatWindow   time window for the repeated download in milliseconds
     * @param repeatListener listener for download repeat task completion & reports
     */
    fun startDownloadRepeat(
        uri: String,
        repeatWindow: Int,
        repeatListener: IRepeatListener?
    ) {
        val reportPeriodMillis =
            if (mReportInterval != -1) mReportInterval else DEFAULT_REPEAT_INTERVAL
        startDownloadRepeat(uri, repeatWindow, reportPeriodMillis, repeatListener)
    }

    /**
     * Start repeat download task.
     *
     * @param uri                uri to fetch to download file
     * @param repeatWindow       time window for the repeated download in milliseconds
     * @param reportPeriodMillis time interval between each report in milliseconds
     * @param repeatListener     listener for download repeat task completion & reports
     */
    fun startDownloadRepeat(
        uri: String,
        repeatWindow: Int,
        reportPeriodMillis: Int,
        repeatListener: IRepeatListener?
    ) {
        repeatWrapper.startDownloadRepeat(uri, repeatWindow, reportPeriodMillis, repeatListener)
    }

    /**
     * Start repeat upload task.
     *
     * @param uri            uri to fetch to download file
     * @param repeatWindow   time window for the repeated upload in milliseconds
     * @param fileSizeOctet  file size in octet
     * @param repeatListener listener for upload repeat task completion & reports
     */
    fun startUploadRepeat(
        uri: String,
        repeatWindow: Int,
        fileSizeOctet: Int,
        repeatListener: IRepeatListener?
    ) {
        val reportPeriodMillis =
            if (mReportInterval != -1) mReportInterval else DEFAULT_REPEAT_INTERVAL
        startUploadRepeat(
            uri,
            repeatWindow,
            reportPeriodMillis,
            fileSizeOctet,
            repeatListener
        )
    }

    /**
     * Start repeat upload task.
     *
     * @param uri                uri to fetch to download file
     * @param repeatWindow       time window for the repeated upload in milliseconds
     * @param reportPeriodMillis time interval between each report in milliseconds
     * @param fileSizeOctet      file size in octet
     * @param repeatListener     listener for upload repeat task completion & reports
     */
    fun startUploadRepeat(
        uri: String,
        repeatWindow: Int,
        reportPeriodMillis: Int,
        fileSizeOctet: Int,
        repeatListener: IRepeatListener?
    ) {
        repeatWrapper.startUploadRepeat(
            uri,
            repeatWindow,
            reportPeriodMillis,
            fileSizeOctet,
            repeatListener
        )
    }

    /**
     * close mSocket + shutdown thread pool.
     */
    override fun forceStopTask() {
        repeatWrapper.cleanTimer()
        mTask.forceStopTask()
        mTask.closeSocket()
        shutdownAndWait()
    }

    /**
     * Get live report.
     *
     * @return download/upload report
     */
    override val liveReport: SpeedTestReport?
        get() = if (speedTestMode == SpeedTestMode.DOWNLOAD) {
            mTask.getReport(SpeedTestMode.DOWNLOAD)
        } else {
            mTask.getReport(SpeedTestMode.UPLOAD)
        }

    override fun closeSocket() {
        mTask.closeSocket()
    }

    /**
     * retrieve current speed test mode.
     *
     * @return speed test mode (UPLOAD/DOWNLOAD/NONE)
     */
    val speedTestMode: SpeedTestMode?
        get() = mTask.speedTestMode
    /**
     * get socket timeout in milliseconds ( 0 if no timeout not defined).
     *
     * @return mSocket timeout value (0 if not defined)
     */
    /**
     * set socket timeout in millisecond.
     *
     * @param socketTimeoutMillis mSocket timeout value in milliseconds
     */
    override var socketTimeout: Int
        get() = mSocketTimeout
        set(socketTimeoutMillis) {
            if (socketTimeoutMillis >= 0) {
                mSocketTimeout = socketTimeoutMillis
            }
        }

    /**
     * Clear all listeners.
     */
    fun clearListeners() {
        mListenerList.clear()
    }

    companion object {
        /**
         * default repeat interval in milliseconds.
         */
        private const val DEFAULT_REPEAT_INTERVAL = 1000
    }
}