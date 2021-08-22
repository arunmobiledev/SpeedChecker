/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.model

/**
 * FTP local mode
 * @author Bertrand Martel
 */
enum class FtpMode {
    //https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html#enterLocalActiveMode()
    ACTIVE,  //https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html#enterLocalPassiveMode()
    PASSIVE
}