/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.utils

import com.assignment.speedchecker.util.speedtest.inter.ISpeedTestListener
import com.assignment.speedchecker.util.speedtest.model.UploadStorageType
import com.assignment.speedchecker.util.speedtest.model.SpeedTestError
import com.assignment.speedchecker.util.speedtest.SpeedTestConst
import kotlin.Throws
import com.assignment.speedchecker.util.speedtest.inter.ISpeedTestSocket
import com.assignment.speedchecker.util.speedtest.protocol.http.states.HttpStates
import com.assignment.speedchecker.util.speedtest.protocol.http.HttpFrame
import java.io.IOException
import java.io.RandomAccessFile
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*

/**
 * Speed Test utility functions.
 *
 * @author Bertrand Martel
 */
object SpeedTestUtils {
    /**
     * random number.
     */
    private val random = SecureRandom()

    /**
     * Generate a random file name for file FTP upload.
     *
     * @return random file name
     */
    fun generateFileName(): String {
        return BigInteger(130, random).toString(32)
    }

    /**
     * dispatch error listener according to errors.
     *
     * @param forceCloseSocket define if interruption callback must be called
     * @param listenerList     list of speed test listeners
     * @param errorMessage     error message from Exception
     */
    @JvmStatic
    fun dispatchError(
        speedTestSocket: ISpeedTestSocket,
        forceCloseSocket: Boolean,
        listenerList: List<ISpeedTestListener>,
        errorMessage: String?
    ) {
        if (!forceCloseSocket) {
            for (i in listenerList.indices) {
                if (errorMessage != null) {
                    listenerList[i].onError(SpeedTestError.CONNECTION_ERROR, errorMessage)
                }
            }
        } else {
            for (i in listenerList.indices) {
                speedTestSocket.liveReport?.let { listenerList[i].onCompletion(it) }
            }
        }
    }

    @JvmStatic
    fun dispatchError(
        speedTestSocket: ISpeedTestSocket,
        forceCloseSocket: Boolean,
        listenerList: List<ISpeedTestListener>,
        error: SpeedTestError,
        errorMessage: String
    ) {
        if (!forceCloseSocket) {
            for (i in listenerList.indices) {
                listenerList[i].onError(error, errorMessage)
            }
        } else {
            for (i in listenerList.indices) {
                speedTestSocket.liveReport?.let { listenerList[i].onCompletion(it) }
            }
        }
    }

    /**
     * Read data from RAM of FILE storage for upload task.
     *
     * @param storageType        RAM or FILE storage
     * @param body               full upload body for RAM storage case
     * @param uploadFile         file pointer to upload for FILE storage case
     * @param uploadTempFileSize temporary file size (offset)
     * @param chunkSize          chunk size to read
     * @return byte array to flush
     */
    @Throws(IOException::class)
    @JvmStatic
    fun readUploadData(
        storageType: UploadStorageType?,
        body: ByteArray?,
        uploadFile: RandomAccessFile?,
        uploadTempFileSize: Int,
        chunkSize: Int
    ): ByteArray {
        val data: ByteArray
        if (storageType == UploadStorageType.RAM_STORAGE) {
            data = Arrays.copyOfRange(
                body, uploadTempFileSize,
                uploadTempFileSize + chunkSize
            )
        } else {
            data = ByteArray(chunkSize)
            uploadFile!!.seek(uploadTempFileSize.toLong())
            uploadFile.read(data)
        }
        return data
    }

    /**
     * dispatch socket timeout error.
     *
     * @param forceCloseSocket define if interruption callback must be called
     * @param listenerList     list of speed test listeners
     * @param errorMessage     error message
     */
    @JvmStatic
    fun dispatchSocketTimeout(
        forceCloseSocket: Boolean,
        listenerList: List<ISpeedTestListener>,
        errorMessage: String
    ) {
        if (!forceCloseSocket) {
            for (i in listenerList.indices) {
                listenerList[i].onError(SpeedTestError.SOCKET_TIMEOUT, errorMessage)
            }
        }
    }

    /**
     * check for http uri error.
     *
     * @param forceCloseSocket define if interruption callback must be called
     * @param listenerList     list of speed test listeners
     * @param httFrameState    http frame state to check
     */
    @JvmStatic
    fun checkHttpFrameError(
        forceCloseSocket: Boolean,
        listenerList: List<ISpeedTestListener>,
        httFrameState: HttpStates?
    ) {
        if (httFrameState != HttpStates.HTTP_FRAME_OK && !forceCloseSocket) {
            for (i in listenerList.indices) {
                listenerList[i].onError(
                    SpeedTestError.INVALID_HTTP_RESPONSE,
                    SpeedTestConst.PARSING_ERROR +
                            "http frame"
                )
            }
        }
    }

    /**
     * check for http header error.
     *
     * @param forceCloseSocket define if interruption callback must be called
     * @param listenerList     list of speed test listeners
     * @param httpHeaderState  http frame state to check
     */
    @JvmStatic
    fun checkHttpHeaderError(
        forceCloseSocket: Boolean,
        listenerList: List<ISpeedTestListener>,
        httpHeaderState: HttpStates?
    ) {
        if (httpHeaderState != HttpStates.HTTP_FRAME_OK && !forceCloseSocket) {
            for (i in listenerList.indices) {
                listenerList[i].onError(
                    SpeedTestError.INVALID_HTTP_RESPONSE,
                    SpeedTestConst.PARSING_ERROR +
                            "http headers"
                )
            }
        }
    }

    /**
     * check for http content length error.
     *
     * @param forceCloseSocket define if interruption callback must be called
     * @param listenerList     list of speed test listeners
     * @param httpFrame        http frame state to check
     */
    @JvmStatic
    fun checkHttpContentLengthError(
        forceCloseSocket: Boolean,
        listenerList: List<ISpeedTestListener>,
        httpFrame: HttpFrame
    ) {
        if (httpFrame.contentLength <= 0 && !forceCloseSocket) {
            for (i in listenerList.indices) {
                listenerList[i].onError(
                    SpeedTestError.INVALID_HTTP_RESPONSE, "Error content length " +
                            "is inconsistent"
                )
            }
        }
    }
}