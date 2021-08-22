/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.utils

import com.assignment.speedchecker.util.speedtest.SpeedTestConst
import kotlin.Throws
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

/**
 * Generate Random byte array, file for randomly generated uploaded file.
 *
 * @author Bertrand Martel
 */
class RandomGen {
    /**
     * Random object.
     */
    private val mRandom = Random()

    /**
     * Random generated file.
     */
    private var mFile: File? = null

    /**
     * Generate random byte array.
     *
     * @param length number of bytes to be generated
     * @return random byte array
     */
    fun generateRandomArray(length: Int): ByteArray {
        val buffer = ByteArray(length)
        val iter = length / SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK
        val remain = length % SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK
        for (i in 0 until iter) {
            val random = ByteArray(SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK)
            mRandom.nextBytes(random)
            System.arraycopy(
                random,
                0,
                buffer,
                i * SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK,
                SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK
            )
        }
        if (remain > 0) {
            val random = ByteArray(remain)
            mRandom.nextBytes(random)
            System.arraycopy(
                random,
                0,
                buffer,
                iter * SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK,
                remain
            )
        }
        return buffer
    }

    /**
     * Generate random file.
     *
     * @param length number of bytes to be generated
     * @return file with random content
     */
    @Throws(IOException::class)
    fun generateRandomFile(length: Int): RandomAccessFile {
        mFile = File.createTempFile(
            SpeedTestConst.UPLOAD_TEMP_FILE_NAME,
            SpeedTestConst.UPLOAD_TEMP_FILE_EXTENSION
        )
        val randomFile = RandomAccessFile(mFile!!.absolutePath, "rw")
        randomFile.setLength(length.toLong())
        val iter = length / SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK
        val remain = length % SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK
        for (i in 0 until iter) {
            val random = ByteArray(SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK)
            mRandom.nextBytes(random)
            randomFile.write(random)
        }
        if (remain > 0) {
            val random = ByteArray(remain)
            mRandom.nextBytes(random)
            randomFile.write(random)
        }
        return randomFile
    }

    /**
     * Delete random file.
     */
    fun deleteFile() {
        if (mFile != null) {
            mFile!!.delete()
        }
    }
}