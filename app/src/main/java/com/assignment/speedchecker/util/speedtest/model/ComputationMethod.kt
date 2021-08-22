/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.model

/**
 * Computation method type used to compute transfer rate.
 *
 * @author Bertrand Martel
 */
enum class ComputationMethod {
    /**
     * the transfer rate value is computed since the beginning (all packets are take into account).
     */
    MEDIAN_ALL_TIME,

    /**
     * the transfer rate value is computed since the last time interval (since last onProgress callback).
     */
    MEDIAN_INTERVAL
}