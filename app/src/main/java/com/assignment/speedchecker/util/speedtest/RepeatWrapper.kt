/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest

import com.assignment.speedchecker.util.speedtest.inter.ISpeedTestListener
import com.assignment.speedchecker.util.speedtest.model.SpeedTestError
import com.assignment.speedchecker.util.speedtest.inter.ISpeedTestSocket
import com.assignment.speedchecker.util.speedtest.model.SpeedTestMode
import com.assignment.speedchecker.util.speedtest.inter.IRepeatListener
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Repeat tasks speed test wrapper : this is used to repeat download/upload requests during a fix duration.
 *
 * @author Bertrand Martel
 */
class RepeatWrapper
/**
 * Build Speed test repeat.
 *
 * @param socket speed test socket
 */(
    /**
     * speed test socket interface.
     */
    private val mSpeedTestSocket: ISpeedTestSocket
) {
    /**
     * transfer rate list.
     */
    private var mRepeatTransferRateList = Collections.synchronizedList(ArrayList<BigDecimal?>())

    /**
     * define if download repeat task is finished.
     */
    private var mRepeatFinished = false

    /**
     * number of packet downloaded for download/upload repeat task.
     */
    private var mRepeatTempPckSize: Long = 0
    /**
     * Check if upload repeat task is running.
     *
     * @return repeat upload status
     */
    /**
     * define if upload should be repeated.
     */
    var isRepeatUpload = false
        private set

    /**
     * start time for download repeat task.
     */
    private var mStartDateRepeat: Long = 0

    /**
     * time window for download repeat task.
     */
    private var mRepeatWindows = 0

    /**
     * current number of request for download repeat task.
     */
    private var mRepeatRequestNum = 0
    /**
     * check if download repeat task is running.
     *
     * @return repeat download status
     */
    /**
     * define if download should be repeated.
     */
    var isRepeatDownload = false
        private set

    /**
     * number of packet pending for download repeat task.
     */
    private var mRepeatPacketSize = BigDecimal.ZERO

    /**
     * define if the first download repeat has been sent and waiting for connection
     * It is reset to false when the client is connected to server the first time.
     */
    private var mFirstDownloadRepeat = false

    /**
     * define if the first upload repeat has been sent and waiting for connection
     * It is reset to false when the client is connected to server the first time.
     */
    private var mFirstUploadRepeat = false

    /**
     * Timer used in repeat methods.
     */
    private var mTimer: Timer? = null

    /**
     * Build repeat download/upload report based on stats on all packets downlaoded until now.
     *
     * @param scale             scale value for bigdecimal
     * @param roundingMode      rounding mode used for bigdecimal
     * @param speedTestMode     speed test mode
     * @param reportTime        time of current download
     * @param transferRateOctet transfer rate in octet/s
     * @return speed test report object
     */
    fun getRepeatReport(
        scale: Int,
        roundingMode: RoundingMode?,
        speedTestMode: SpeedTestMode?,
        reportTime: Long,
        transferRateOctet: BigDecimal
    ): SpeedTestReport {
        var temporaryPacketSize: Long
        var downloadRepeatRateOctet = transferRateOctet
        var downloadRepeatReportTime = reportTime
        var progressPercent: BigDecimal = if (mStartDateRepeat != 0L) {
            if (!mRepeatFinished) {
                val test = System.nanoTime() - mStartDateRepeat
                BigDecimal(test).multiply(SpeedTestConst.PERCENT_MAX)
                    .divide(
                        BigDecimal(mRepeatWindows).multiply(BigDecimal(1000000)),
                        scale,
                        roundingMode
                    )
            } else {
                SpeedTestConst.PERCENT_MAX
            }
        } else {
            //download has not started yet
            BigDecimal.ZERO
        }
        var rates = BigDecimal.ZERO
        for (rate in mRepeatTransferRateList) {
            rates = rates.add(rate)
        }
        if (!mRepeatTransferRateList.isEmpty()) {
            downloadRepeatRateOctet = rates.add(downloadRepeatRateOctet).divide(
                BigDecimal(
                    mRepeatTransferRateList
                        .size
                ).add(
                    BigDecimal(mRepeatTempPckSize).divide(mRepeatPacketSize, scale, roundingMode)
                ), scale, roundingMode
            )
        }
        val transferRateBit = downloadRepeatRateOctet.multiply(SpeedTestConst.BIT_MULTIPLIER)
        if (!mRepeatFinished) {
            temporaryPacketSize = mRepeatTempPckSize
        } else {
            temporaryPacketSize = mRepeatTempPckSize
            downloadRepeatReportTime = BigDecimal(mStartDateRepeat).add(
                BigDecimal(mRepeatWindows).multiply(
                    BigDecimal(1000000)
                )
            ).toLong()
        }
        return SpeedTestReport(
            speedTestMode,
            progressPercent.toFloat(),
            mStartDateRepeat,
            downloadRepeatReportTime,
            temporaryPacketSize,
            mRepeatPacketSize.longValueExact(),
            downloadRepeatRateOctet,
            transferRateBit,
            mRepeatRequestNum
        )
    }

    /**
     * Start download for download repeat.
     *
     * @param uri uri to fetch to download file
     */
    private fun startDownloadRepeat(uri: String) {
        isRepeatDownload = true
        mSpeedTestSocket.startDownload(uri)
    }

    /**
     * Start upload for upload repeat.
     *
     * @param uri uri to fetch to upload file
     * @fileSizeOctet file size in octet
     */
    private fun startUploadRepeat(uri: String, fileSizeOctet: Int) {
        mSpeedTestSocket.startUpload(uri, fileSizeOctet)
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
        initRepeat(true)
        mTimer = Timer()
        val speedTestListener: ISpeedTestListener = object : ISpeedTestListener {
            override fun onCompletion(report: SpeedTestReport) {
                mRepeatTransferRateList.add(report.transferRateOctet)
                startDownloadRepeat(uri)
                mRepeatRequestNum++
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                //nothing to do here for download repeat task listener
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                clearRepeatTask(this)
            }
        }
        mSpeedTestSocket.addSpeedTestListener(speedTestListener)
        mRepeatWindows = repeatWindow
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mSpeedTestSocket.removeSpeedTestListener(speedTestListener)
                mSpeedTestSocket.forceStopTask()
                cleanTimer()
                mRepeatFinished = true
                repeatListener?.onCompletion(mSpeedTestSocket.liveReport)
            }
        }, repeatWindow.toLong())
        mTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                repeatListener?.onReport(mSpeedTestSocket.liveReport)
            }
        }, reportPeriodMillis.toLong(), reportPeriodMillis.toLong())
        startDownloadRepeat(uri)
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
        initRepeat(false)
        mTimer = Timer()
        val speedTestListener: ISpeedTestListener = object : ISpeedTestListener {
            override fun onProgress(percent: Float, report: SpeedTestReport) {
                //nothing to do here for upload repeat task listener
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                clearRepeatTask(this)
            }

            override fun onCompletion(report: SpeedTestReport) {
                mRepeatTransferRateList.add(report.transferRateOctet)
                startUploadRepeat(uri, fileSizeOctet)
                mRepeatRequestNum++
            }
        }
        mSpeedTestSocket.addSpeedTestListener(speedTestListener)
        mRepeatWindows = repeatWindow
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mSpeedTestSocket.removeSpeedTestListener(speedTestListener)
                mSpeedTestSocket.forceStopTask()
                cleanTimer()
                mRepeatFinished = true
                repeatListener?.onCompletion(mSpeedTestSocket.liveReport)
            }
        }, repeatWindow.toLong())
        mTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                repeatListener?.onReport(mSpeedTestSocket.liveReport)
            }
        }, reportPeriodMillis.toLong(), reportPeriodMillis.toLong())
        startUploadRepeat(uri, fileSizeOctet)
    }

    fun cleanTimer() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer!!.purge()
        }
    }

    /**
     * Initialize download/upload repeat task variables for report + state.
     *
     * @param isDownload define if initialization is for download or upload
     */
    private fun initRepeat(isDownload: Boolean) {
        isRepeatDownload = isDownload
        mFirstDownloadRepeat = isDownload
        isRepeatUpload = !isDownload
        mFirstUploadRepeat = !isDownload
        initRepeatVars()
    }

    /**
     * Initialize upload/download repeat task variables for report + state.
     */
    private fun initRepeatVars() {
        mRepeatRequestNum = 0
        mRepeatPacketSize = BigDecimal.ZERO
        mRepeatTempPckSize = 0
        mRepeatFinished = false
        mStartDateRepeat = 0
        mRepeatTransferRateList = ArrayList()
    }

    /**
     * Clear completly download/upload repeat task.
     *
     * @param listener speed test listener
     */
    private fun clearRepeatTask(listener: ISpeedTestListener) {
        mSpeedTestSocket.removeSpeedTestListener(listener)
        cleanTimer()
        mRepeatFinished = true
        mSpeedTestSocket.closeSocket()
        mSpeedTestSocket.shutdownAndWait()
    }

    /**
     * Check if this is the first packet to be downloaded for repeat operation.
     *
     * @return state for first download
     */
    val isFirstDownload: Boolean
        get() = mFirstDownloadRepeat && isRepeatDownload

    /**
     * Check if this is the first packet to be uploaded for repeat operation.
     *
     * @return state of first upload
     */
    val isFirstUpload: Boolean
        get() = mFirstUploadRepeat && isRepeatUpload

    /**
     * Set the first downloaded packet status.
     *
     * @param state download repeat status
     */
    fun setFirstDownloadRepeat(state: Boolean) {
        mFirstDownloadRepeat = state
    }

    /**
     * Set the start date for repeat task.
     *
     * @param timeStart start date in millis
     */
    fun setStartDate(timeStart: Long) {
        mStartDateRepeat = timeStart
    }

    /**
     * Update total packet size to be downloaded/uploaded.
     *
     * @param packetSize packet size in octet
     */
    fun updatePacketSize(packetSize: BigDecimal?) {
        mRepeatPacketSize = mRepeatPacketSize.add(packetSize)
    }

    /**
     * Update temporary packet size currently downloaded/uploaded.
     *
     * @param read packet size in octet
     */
    fun updateTempPacketSize(read: Int) {
        mRepeatTempPckSize += read.toLong()
    }

    /**
     * Check if repeat task is running.
     *
     * @return repeat status
     */
    val isRepeat: Boolean
        get() = isRepeatDownload || isRepeatUpload

    /**
     * Set the first uploaded packet status.
     *
     * @param state first upload repeat status
     */
    fun setFirstUploadRepeat(state: Boolean) {
        mFirstUploadRepeat = state
    }
}