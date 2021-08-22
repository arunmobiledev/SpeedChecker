/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.model

/**
 * Feature Speed Test common Error code.
 *
 * @author Bertrand Martel
 */
enum class SpeedTestError {
    /**
     * invalid http response was returned.
     */
    INVALID_HTTP_RESPONSE,

    /**
     * socket error occured.
     */
    SOCKET_ERROR,

    /**
     * socket timeout occured.
     */
    SOCKET_TIMEOUT,

    /**
     * connection error occured.
     */
    CONNECTION_ERROR,

    /**
     * Malformed URI
     */
    MALFORMED_URI,

    /**
     * protocol can be FTP or HTTP
     */
    UNSUPPORTED_PROTOCOL
}