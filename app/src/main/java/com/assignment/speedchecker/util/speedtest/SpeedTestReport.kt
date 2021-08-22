/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest

import com.assignment.speedchecker.util.speedtest.model.SpeedTestMode
import java.math.BigDecimal

/**
 * Speed examples report.
 *
 *
 * feature current report measurement for DOWNLOAD/UPLOAD
 *
 * @author Bertrand Martel
 */
class SpeedTestReport
/**
 * Build Upload report.
 *
 * @param speedTestMode     speed examples mode (DOWNLOAD/UPLOAD)
 * @param progressPercent   speed examples progress in percent (%)
 * @param startTime         upload start time in nanoseconds
 * @param reportTime        upload report time in nanoseconds
 * @param tempPacketSize    current size of file to upload
 * @param totalPacketSize   total file size
 * @param transferRateOctet transfer rate in octet/s
 * @param transferRateBit   transfer rate in bit/s
 * @param requestNum        number of request for this report
 */(
    /**
     * speed examples mode for this report.
     */
    val speedTestMode: SpeedTestMode?,
    /**
     * speed examples progress in percent (%).
     */
    val progressPercent: Float,
    /**
     * upload start time in nanoseconds.
     */
    val startTime: Long,
    /**
     * upload report time in nanoseconds.
     */
    val reportTime: Long,
    /**
     * current size of file to upload.
     */
    val temporaryPacketSize: Long,
    /**
     * total file size.
     */
    val totalPacketSize: Long,
    /**
     * transfer rate in octet/s.
     */
    val transferRateOctet: BigDecimal,
    /**
     * transfer rate in bit/s.
     */
    val transferRateBit: BigDecimal,
    /**
     * number of request.
     */
    val requestNum: Int
) {
    /**
     * get current file size.
     *
     * @return packet size in bit
     */
    /**
     * get total file size.
     *
     * @return total file size in bit
     */
    /**
     * get transfer rate in octet/s.
     *
     * @return transfer rate in octet/s
     */
    /**
     * get transfer rate in bit/s.
     *
     * @return transfer rate in bit/s
     */
    /**
     * get speed examples start time.
     *
     * @return start time timestamp (millis since 1970)
     */
    /**
     * get current timestamp.
     *
     * @return current timestamp for this report measurement (millis since 1970)
     */
    /**
     * get speed examples mode (DOWNLOAD/UPLOAD).
     *
     * @return speed examples mode
     */
    /**
     * get speed examples progress.
     *
     * @return progress in %
     */
    /**
     * get request num.
     *
     * @return http request number
     */

}