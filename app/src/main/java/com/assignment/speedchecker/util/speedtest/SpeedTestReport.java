/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util.speedtest;

import com.assignment.speedchecker.util.speedtest.model.SpeedTestMode;

import java.math.BigDecimal;

/**
 * Speed examples report.
 * <p/>
 * feature current report measurement for DOWNLOAD/UPLOAD
 *
 * @author Bertrand Martel
 */
public class SpeedTestReport {

    /**
     * current size of file to upload.
     */
    private final long mTempPacketSize;

    /**
     * total file size.
     */
    private final long mTotalPacketSize;

    /**
     * transfer rate in octet/s.
     */
    private final BigDecimal mTransferRateOctet;

    /**
     * transfer rate in bit/s.
     */
    private final BigDecimal mTransferRateBit;

    /**
     * upload start time in nanoseconds.
     */
    private final long mStartTime;

    /**
     * upload report time in nanoseconds.
     */
    private final long mReportTime;

    /**
     * speed examples mode for this report.
     */
    private final SpeedTestMode mSpeedTestMode;

    /**
     * speed examples progress in percent (%).
     */
    private final float mProgressPercent;

    /**
     * number of request.
     */
    private final int mRequestNum;

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
     */
    public SpeedTestReport(final SpeedTestMode speedTestMode,
                           final float progressPercent,
                           final long startTime,
                           final long reportTime,
                           final long tempPacketSize,
                           final long totalPacketSize,
                           final BigDecimal transferRateOctet,
                           final BigDecimal transferRateBit,
                           final int requestNum) {

        this.mSpeedTestMode = speedTestMode;
        this.mProgressPercent = progressPercent;
        this.mStartTime = startTime;
        this.mReportTime = reportTime;
        this.mTempPacketSize = tempPacketSize;
        this.mTotalPacketSize = totalPacketSize;
        this.mTransferRateOctet = transferRateOctet;
        this.mTransferRateBit = transferRateBit;
        this.mRequestNum = requestNum;
    }

    /**
     * get current file size.
     *
     * @return packet size in bit
     */
    public long getTemporaryPacketSize() {
        return mTempPacketSize;
    }

    /**
     * get total file size.
     *
     * @return total file size in bit
     */
    public long getTotalPacketSize() {
        return mTotalPacketSize;
    }

    /**
     * get transfer rate in octet/s.
     *
     * @return transfer rate in octet/s
     */
    public BigDecimal getTransferRateOctet() {
        return mTransferRateOctet;
    }

    /**
     * get transfer rate in bit/s.
     *
     * @return transfer rate in bit/s
     */
    public BigDecimal getTransferRateBit() {
        return mTransferRateBit;
    }

    /**
     * get speed examples start time.
     *
     * @return start time timestamp (millis since 1970)
     */
    public long getStartTime() {
        return mStartTime;
    }

    /**
     * get current timestamp.
     *
     * @return current timestamp for this report measurement (millis since 1970)
     */
    public long getReportTime() {
        return mReportTime;
    }

    /**
     * get speed examples mode (DOWNLOAD/UPLOAD).
     *
     * @return speed examples mode
     */
    public SpeedTestMode getSpeedTestMode() {
        return mSpeedTestMode;
    }

    /**
     * get speed examples progress.
     *
     * @return progress in %
     */
    public float getProgressPercent() {
        return mProgressPercent;
    }

    /**
     * get request num.
     *
     * @return http request number
     */
    public int getRequestNum() {
        return mRequestNum;
    }
}
