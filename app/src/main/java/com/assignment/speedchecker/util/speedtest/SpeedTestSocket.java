/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util.speedtest;

import com.assignment.speedchecker.util.speedtest.inter.IRepeatListener;
import com.assignment.speedchecker.util.speedtest.inter.ISpeedTestListener;
import com.assignment.speedchecker.util.speedtest.inter.ISpeedTestSocket;
import com.assignment.speedchecker.util.speedtest.model.ComputationMethod;
import com.assignment.speedchecker.util.speedtest.model.FtpMode;
import com.assignment.speedchecker.util.speedtest.model.SpeedTestMode;
import com.assignment.speedchecker.util.speedtest.model.UploadStorageType;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Client socket main implementation.
 * <p/>
 * Two modes upload and download
 * <p/>
 * upload will write a file to a specific host with given uri. The file is
 * randomly generated with a given size
 * <p/>
 * download will retrieve a content from a specific host with given uri.
 * <p/>
 * For both mode, transfer rate is calculated independently from mSocket initial
 * connection
 *
 * @author Bertrand Martel
 */
public class SpeedTestSocket implements ISpeedTestSocket {

    /**
     * BigDecimal scale used in transfer rate calculation.
     */
    private int mScale = SpeedTestConst.DEFAULT_SCALE;

    /**
     * BigDecimal RoundingMode used in transfer rate calculation.
     */
    private RoundingMode mRoundingMode = SpeedTestConst.DEFAULT_ROUNDING_MODE;

    /**
     * FTP mode passive or active.
     */
    private FtpMode mFtpMode = FtpMode.PASSIVE;

    /**
     * Upload storage type.
     */
    private UploadStorageType mUploadStorageType = UploadStorageType.RAM_STORAGE;

    /**
     * speed test listener list.
     */
    private final List<ISpeedTestListener> mListenerList = new ArrayList<>();

    /**
     * this is the size of each data sent to upload server.
     */
    private int mUploadChunkSize = SpeedTestConst.DEFAULT_UPLOAD_SIZE;

    /**
     * mSocket timeout.
     */
    private int mSocketTimeout = SpeedTestConst.DEFAULT_SOCKET_TIMEOUT;

    /**
     * Speed test repeat wrapper.
     */
    private final RepeatWrapper mRepeatWrapper = new RepeatWrapper(this);

    /**
     * Speed tets task object used to manage download/upload operations.
     */
    private final SpeedTestTask mTask = new SpeedTestTask(this, mListenerList);

    /**
     * setup time for calculating the threshold before updating the calculation of download.
     */
    private long mDownloadSetupTime = SpeedTestConst.DEFAULT_DOWNLOAD_SETUP_TIME;

    /**
     * setup time for calculating the threshold before updating the calculation of upload.
     */
    private long mUploadSetupTime = SpeedTestConst.DEFAULT_UPLOAD_SETUP_TIME;

    /**
     * report interval in milliseconds.
     */
    private int mReportInterval = -1;

    /**
     * Computation method used to calculate transfer rate.
     */
    private ComputationMethod mComputationMethod = ComputationMethod.MEDIAN_ALL_TIME;

    /**
     * default repeat interval in milliseconds.
     */
    private final static int DEFAULT_REPEAT_INTERVAL = 1000;

    public SpeedTestSocket() {

    }

    /**
     * Initialize global report interval value.
     *
     * @param reportInterval report value in milliseconds
     */
    public SpeedTestSocket(final int reportInterval) {
        mReportInterval = reportInterval;
    }

    /**
     * initialize report task.
     *
     * @param reportInterval report interval in milliseconds
     */
    private void initReportTask(final int reportInterval) {

        mTask.renewReportThreadPool();

        mTask.getReportThreadPool().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final SpeedTestReport report = getLiveReport();

                for (final ISpeedTestListener listener : mListenerList) {
                    listener.onProgress(report.getProgressPercent(), report);
                }
            }
        }, reportInterval, reportInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * Add a speed test listener to list.
     *
     * @param listener speed test listener to be added
     */
    @Override
    public void addSpeedTestListener(final ISpeedTestListener listener) {
        mListenerList.add(listener);
    }

    /**
     * Relive a speed listener from list.
     *
     * @param listener speed test listener to be removed
     */
    @Override
    public void removeSpeedTestListener(final ISpeedTestListener listener) {
        mListenerList.remove(listener);
    }

    /**
     * Shutdown threadpool and wait for task completion.
     */
    @Override
    public void shutdownAndWait() {
        mTask.shutdownAndWait();
    }

    /**
     * Start download process with a fixed duration.
     *
     * @param uri         uri to fetch to download file
     * @param maxDuration maximum duration of the speed test in milliseconds
     */
    public void startFixedDownload(final String uri,
                                   final int maxDuration) {

        if (mReportInterval != -1 && !mTask.isReportInterval()) {
            initReportTask(mReportInterval);
            mTask.setReportInterval(true);
        }
        mTask.renewReportThreadPool();

        mTask.getReportThreadPool().schedule(new Runnable() {
            @Override
            public void run() {
                forceStopTask();
            }
        }, maxDuration, TimeUnit.MILLISECONDS);

        startDownload(uri);
    }

    /**
     * Start download process with a fixed duration.
     *
     * @param uri            uri to fetch to download file
     * @param maxDuration    maximum duration of the speed test in milliseconds
     * @param reportInterval report interval in milliseconds
     */
    public void startFixedDownload(final String uri,
                                   final int maxDuration,
                                   final int reportInterval) {
        initReportTask(reportInterval);
        mTask.setReportInterval(true);
        startFixedDownload(uri, maxDuration);
    }

    /**
     * Start download process.
     *
     * @param uri            uri to fetch to download file
     * @param reportInterval report interval in milliseconds
     */
    public void startDownload(final String uri,
                              final int reportInterval) {
        initReportTask(reportInterval);
        mTask.setReportInterval(true);
        startDownload(uri);
    }

    /**
     * Start download process.
     *
     * @param uri uri to fetch to download file
     */
    @Override
    public void startDownload(final String uri) {
        if (mReportInterval != -1 && !mTask.isReportInterval()) {
            initReportTask(mReportInterval);
            mTask.setReportInterval(true);
        }
        mTask.startDownloadRequest(uri);
    }

    /**
     * Set proxy server for all DL/UL tasks.
     *
     * @param proxyUrl proxy URL
     * @return false if malformed
     */
    @Override
    public boolean setProxyServer(final String proxyUrl) {
        return mTask.setProxy(proxyUrl);
    }

    /**
     * Start upload process.
     *
     * @param uri           uri to fetch
     * @param fileSizeOctet size of file to upload
     * @param maxDuration   maximum duration of speed test in milliseconds
     */
    public void startFixedUpload(final String uri,
                                 final int fileSizeOctet,
                                 final int maxDuration) {

        if (mReportInterval != -1 && !mTask.isReportInterval()) {
            initReportTask(mReportInterval);
            mTask.setReportInterval(true);
        }
        mTask.renewReportThreadPool();

        mTask.getReportThreadPool().schedule(new Runnable() {
            @Override
            public void run() {
                forceStopTask();
            }
        }, maxDuration, TimeUnit.MILLISECONDS);

        startUpload(uri, fileSizeOctet);
    }

    /**
     * Start upload process.
     *
     * @param uri            uri to fetch
     * @param fileSizeOctet  size of file to upload
     * @param maxDuration    maximum duration of speed test in milliseconds
     * @param reportInterval report interval in milliseconds
     */
    public void startFixedUpload(
            final String uri,
            final int fileSizeOctet,
            final int maxDuration,
            final int reportInterval) {

        initReportTask(reportInterval);
        mTask.setReportInterval(true);
        startFixedUpload(uri, fileSizeOctet, maxDuration);
    }

    /**
     * Start upload process.
     *
     * @param uri            uri to fetch
     * @param fileSizeOctet  size of file to upload
     * @param reportInterval report interval in milliseconds
     */
    public void startUpload(
            final String uri,
            final int fileSizeOctet,
            final int reportInterval) {

        initReportTask(reportInterval);
        mTask.setReportInterval(true);
        startUpload(uri, fileSizeOctet);
    }

    /**
     * Start upload process.
     *
     * @param uri           uri to fetch
     * @param fileSizeInBytes size of file to upload
     */
    @Override
    public void startUpload(final String uri, final int fileSizeInBytes) {
        if (mReportInterval != -1 && !mTask.isReportInterval()) {
            initReportTask(mReportInterval);
            mTask.setReportInterval(true);
        }
        mTask.startUploadRequest(uri, fileSizeInBytes);
    }

    /**
     * Start repeat download task.
     *
     * @param uri            uri to fetch to download file
     * @param repeatWindow   time window for the repeated download in milliseconds
     * @param repeatListener listener for download repeat task completion & reports
     */
    public void startDownloadRepeat(
            final String uri,
            final int repeatWindow,
            final IRepeatListener repeatListener) {

        final int reportPeriodMillis = (mReportInterval != -1) ? mReportInterval : DEFAULT_REPEAT_INTERVAL;

        startDownloadRepeat(uri, repeatWindow, reportPeriodMillis, repeatListener);
    }

    /**
     * Start repeat download task.
     *
     * @param uri                uri to fetch to download file
     * @param repeatWindow       time window for the repeated download in milliseconds
     * @param reportPeriodMillis time interval between each report in milliseconds
     * @param repeatListener     listener for download repeat task completion & reports
     */
    public void startDownloadRepeat(
            final String uri,
            final int repeatWindow,
            final int reportPeriodMillis,
            final IRepeatListener repeatListener) {
        mRepeatWrapper.startDownloadRepeat(uri, repeatWindow, reportPeriodMillis, repeatListener);
    }

    /**
     * Start repeat upload task.
     *
     * @param uri            uri to fetch to download file
     * @param repeatWindow   time window for the repeated upload in milliseconds
     * @param fileSizeOctet  file size in octet
     * @param repeatListener listener for upload repeat task completion & reports
     */
    public void startUploadRepeat(
            final String uri,
            final int repeatWindow,
            final int fileSizeOctet,
            final IRepeatListener repeatListener) {

        final int reportPeriodMillis = (mReportInterval != -1) ? mReportInterval : DEFAULT_REPEAT_INTERVAL;

        startUploadRepeat(
                uri,
                repeatWindow,
                reportPeriodMillis,
                fileSizeOctet,
                repeatListener);
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
    public void startUploadRepeat(
            final String uri,
            final int repeatWindow,
            final int reportPeriodMillis,
            final int fileSizeOctet,
            final IRepeatListener repeatListener) {

        mRepeatWrapper.startUploadRepeat(
                uri,
                repeatWindow,
                reportPeriodMillis,
                fileSizeOctet,
                repeatListener);
    }

    /**
     * close mSocket + shutdown thread pool.
     */
    @Override
    public void forceStopTask() {
        mRepeatWrapper.cleanTimer();
        mTask.forceStopTask();
        mTask.closeSocket();
        shutdownAndWait();
    }

    /**
     * Get live report.
     *
     * @return download/upload report
     */
    @Override
    public SpeedTestReport getLiveReport() {
        if (getSpeedTestMode() == SpeedTestMode.DOWNLOAD) {
            return mTask.getReport(SpeedTestMode.DOWNLOAD);
        } else {
            return mTask.getReport(SpeedTestMode.UPLOAD);
        }
    }

    @Override
    public void closeSocket() {
        mTask.closeSocket();
    }

    /**
     * retrieve current speed test mode.
     *
     * @return speed test mode (UPLOAD/DOWNLOAD/NONE)
     */
    public SpeedTestMode getSpeedTestMode() {
        return mTask.getSpeedTestMode();
    }

    /**
     * set socket timeout in millisecond.
     *
     * @param socketTimeoutMillis mSocket timeout value in milliseconds
     */
    public void setSocketTimeout(final int socketTimeoutMillis) {
        if (socketTimeoutMillis >= 0) {
            mSocketTimeout = socketTimeoutMillis;
        }
    }

    /**
     * get socket timeout in milliseconds ( 0 if no timeout not defined).
     *
     * @return mSocket timeout value (0 if not defined)
     */
    @Override
    public int getSocketTimeout() {
        return mSocketTimeout;
    }

    /**
     * retrieve size of each packet sent to upload server.
     *
     * @return size of each packet sent to upload server
     */
    @Override
    public int getUploadChunkSize() {
        return mUploadChunkSize;
    }

    @Override
    public RepeatWrapper getRepeatWrapper() {
        return mRepeatWrapper;
    }

    /**
     * set size of each packet sent to upload server.
     *
     * @param uploadChunkSize new size of each packet sent to upload server
     */
    public void setUploadChunkSize(final int uploadChunkSize) {
        this.mUploadChunkSize = uploadChunkSize;
    }

    /**
     * Set the default RoundingMode for BigDecimal.
     *
     * @param roundingMode rounding mode.
     */
    public void setDefaultRoundingMode(final RoundingMode roundingMode) {
        this.mRoundingMode = roundingMode;
    }

    /**
     * Set the default scale for BigDecimal.
     *
     * @param scale mScale value
     */
    public void setDefaultScale(final int scale) {
        this.mScale = scale;
    }

    /**
     * Set computation method used to calculate transfer rate.
     *
     * @param computationMethod model value
     */
    @Override
    public void setComputationMethod(final ComputationMethod computationMethod) {
        mComputationMethod = computationMethod;
    }

    /**
     * Set FTP mode passive or active.
     *
     * @param mode ftp mode.
     */
    public void setFtpMode(final FtpMode mode) {
        this.mFtpMode = mode;
    }

    /**
     * Get the computation method.
     *
     * @return computation method
     */
    @Override
    public ComputationMethod getComputationMethod() {
        return mComputationMethod;
    }

    /**
     * Set the setup time for upload.
     *
     * @param setupTime point in time from which upload speed rate should be computed
     */
    public void setUploadSetupTime(final long setupTime) {
        this.mUploadSetupTime = setupTime;
    }

    /**
     * Set the setup time for download.
     *
     * @param setupTime point in time from which download speed rate should be computed
     */
    public void setDownloadSetupTime(final long setupTime) {
        this.mDownloadSetupTime = setupTime;
    }

    /**
     * Get download setup time value.
     *
     * @return download setup time value
     */
    @Override
    public long getDownloadSetupTime() {
        return mDownloadSetupTime;
    }

    /**
     * Get upload setup time value.
     *
     * @return upload setup time value
     */
    @Override
    public long getUploadSetupTime() {
        return mUploadSetupTime;
    }

    /**
     * Retrieve rounding mode used for BigDecimal.
     *
     * @return rounding mode
     */
    @Override
    public RoundingMode getDefaultRoundingMode() {
        return mRoundingMode;
    }

    /**
     * Retrieve scale used for BigDecimal.
     *
     * @return mScale value
     */
    @Override
    public int getDefaultScale() {
        return mScale;
    }

    /**
     * Retrieve upload storage type (RAM or ROM).
     *
     * @return upload storage type
     */
    @Override
    public UploadStorageType getUploadStorageType() {
        return mUploadStorageType;
    }

    /**
     * Set upload storage type.
     *
     * @param uploadStorageType upload storage type
     */
    @Override
    public void setUploadStorageType(final UploadStorageType uploadStorageType) {
        mUploadStorageType = uploadStorageType;
    }

    /**
     * Clear all listeners.
     */
    public void clearListeners() {
        mListenerList.clear();
    }

    /**
     * Get FTP mode.
     */
    @Override
    public FtpMode getFtpMode() {
        return mFtpMode;
    }
}
