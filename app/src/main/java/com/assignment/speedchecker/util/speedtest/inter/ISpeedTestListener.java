/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util.speedtest.inter;

import com.assignment.speedchecker.util.speedtest.SpeedTestReport;
import com.assignment.speedchecker.util.speedtest.model.SpeedTestError;

/**
 * Listener for speed examples output results.
 * <p/>
 * <ul>
 * <li>monitor download process result with transfer rate in bit/s and octet/s</li>
 * <li>monitor download progress</li>
 * <li>monitor upload process result with transfer rate in bit/s and octet/s</li>
 * <li>monitor upload progress</li>
 * </ul>
 *
 * @author Bertrand Martel
 */

public interface ISpeedTestListener {

    /**
     * download/upload process completion with transfer rate in bit/s and octet/s.
     *
     * @param report download speed test report
     */
    void onCompletion(SpeedTestReport report);

    /**
     * monitor download/upload progress.
     *
     * @param percent % of progress
     * @param report  current speed test download report
     */
    void onProgress(float percent, SpeedTestReport report);

    /**
     * Error catch.
     *
     * @param speedTestError error enum
     * @param errorMessage   error message
     */
    void onError(SpeedTestError speedTestError, String errorMessage);
}
