/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util.speedtest;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Constants for Speed Test library.
 *
 * @author Bertrand Martel
 */
public class SpeedTestConst {

    /**
     * maximum size for report thread pool.
     */
    public static final int THREAD_POOL_REPORT_SIZE = 1;

    /**
     * size of the write read buffer for downloading.
     */
    public static final int READ_BUFFER_SIZE = 65535;

    /**
     * default size of each packet sent to upload server.
     */
    public static final int DEFAULT_UPLOAD_SIZE = 65535;

    /**
     * default socket timeout in milliseconds.
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;

    /**
     * time to wait for task to complete when threadpool is shutdown.
     */
    public static final int THREADPOOL_WAIT_COMPLETION_MS = 500;

    /**
     * http ok status code.
     */
    public static final int HTTP_OK = 200;

    /**
     * max value for percent.
     */
    public static final BigDecimal PERCENT_MAX = new BigDecimal("100");

    /**
     * nanosecond divider.
     */
    public static final BigDecimal NANO_DIVIDER = new BigDecimal("1000000000");

    /**
     * bit multiplier value.
     */
    public static final BigDecimal BIT_MULTIPLIER = new BigDecimal("8");

    /**
     * parsing error message.
     */
    public static final String PARSING_ERROR = "Error occurred while parsing ";

    /**
     * writing socket error message.
     */
    public static final String SOCKET_WRITE_ERROR = "Error occurred while writing to socket";

    /**
     * default scale for BigDecimal.
     */
    public static final int DEFAULT_SCALE = 4;

    /**
     * default rounding mode for BigDecimal.
     */
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;

    /**
     * default port for http download/upload.
     */
    public static final int HTTP_DEFAULT_PORT = 80;

    /**
     * default port for FTP download/upload.
     */
    public static final int FTP_DEFAULT_PORT = 21;

    /**
     * default username for FTP download/upload.
     */
    public static final String FTP_DEFAULT_USER = "anonymous";

    /**
     * default password for FTP download/upload.
     */
    public static final String FTP_DEFAULT_PASSWORD = "";

    /**
     * Chunk to write at each iteration for upload file generation.
     */
    public static final int UPLOAD_FILE_WRITE_CHUNK = 64000;

    /**
     * Temporary file name for upload file.
     */
    public static final String UPLOAD_TEMP_FILE_NAME = "speed-test-random";

    /**
     * Temporary file extension.
     */
    public static final String UPLOAD_TEMP_FILE_EXTENSION = ".tmp";

    /**
     * default setup time for download.
     */
    public static final long DEFAULT_DOWNLOAD_SETUP_TIME = 0;

    /**
     * default setup time for upload.
     */
    public static final long DEFAULT_UPLOAD_SETUP_TIME = 0;
}
