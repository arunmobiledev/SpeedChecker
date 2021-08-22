/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util.speedtest.inter;

import com.assignment.speedchecker.util.speedtest.SpeedTestReport;

/**
 * Listener for download repeat completion task + reports.
 *
 * @author Bertrand Martel
 */
public interface IRepeatListener {

    /**
     * called when repeat download task is finished.
     *
     * @param report speed examples report
     */
    void onCompletion(SpeedTestReport report);

    /**
     * called when a speed examples report is sent.
     *
     * @param report speed examples report
     */
    void onReport(SpeedTestReport report);
}
