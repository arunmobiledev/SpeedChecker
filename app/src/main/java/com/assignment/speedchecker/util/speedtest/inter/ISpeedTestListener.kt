/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.inter

import com.assignment.speedchecker.util.speedtest.SpeedTestReport
import com.assignment.speedchecker.util.speedtest.model.SpeedTestError

/**
 * Listener for speed examples output results.
 *
 *
 *
 *  * monitor download process result with transfer rate in bit/s and octet/s
 *  * monitor download progress
 *  * monitor upload process result with transfer rate in bit/s and octet/s
 *  * monitor upload progress
 *
 *
 * @author Bertrand Martel
 */
interface ISpeedTestListener {
    /**
     * download/upload process completion with transfer rate in bit/s and octet/s.
     *
     * @param report download speed test report
     */
    fun onCompletion(report: SpeedTestReport)

    /**
     * monitor download/upload progress.
     *
     * @param percent % of progress
     * @param report  current speed test download report
     */
    fun onProgress(percent: Float, report: SpeedTestReport)

    /**
     * Error catch.
     *
     * @param speedTestError error enum
     * @param errorMessage   error message
     */
    fun onError(speedTestError: SpeedTestError, errorMessage: String)
}