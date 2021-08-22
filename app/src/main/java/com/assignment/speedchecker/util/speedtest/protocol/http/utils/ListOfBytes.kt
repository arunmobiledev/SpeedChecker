/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.utils

import java.io.UnsupportedEncodingException
import java.util.ArrayList

/**
 * List of byte object defining a list of byte array items for resolution of
 * buffer issue when stocking huge byte array or huge string
 *
 * @author Bertrand Martel
 */
class ListOfBytes : IByteList {
    /** common byte array to be managed  */
    private var classicList: MutableList<ByteArray> = ArrayList()

    /** default constructor for list of bytes  */
    constructor() {
        classicList = ArrayList()
    }

    /** construct a list of byte array from a data string  */
    constructor(data: String) {
        classicList = ArrayList()
        try {
            val dataConvertedToByte = data.toByteArray(charset("UTF-8"))
            /* identify content length */
            val length = dataConvertedToByte.size
            if (length > 0) {
                val numberOfBlockToWrite = length % BLOCK_SIZE

                /* define number of block to write */
                var numberOfBlock = 0
                numberOfBlock = if (numberOfBlockToWrite == 0) {
                    length / BLOCK_SIZE
                } else {
                    length / BLOCK_SIZE + 1
                }
                for (i in 0 until numberOfBlock) {
                    if (i == numberOfBlock - 1) {
                        /* this is the last block to write */
                        val size = length - i * BLOCK_SIZE
                        val data1 = ByteArray(size)
                        System.arraycopy(
                            dataConvertedToByte, i * BLOCK_SIZE,
                            data1, 0, size
                        )
                        classicList.add(data1)
                    } else {
                        /* this is not the last block to write */
                        val data1 = ByteArray(BLOCK_SIZE)
                        System.arraycopy(
                            dataConvertedToByte, i * BLOCK_SIZE,
                            data1, 0, BLOCK_SIZE
                        )
                        classicList.add(data1)
                    }
                }
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    /**
     * Return all byte for classic List
     *
     * @return byte array
     */
    override val bytes: ByteArray
        get() {
            var size = 0
            for (i in classicList.indices) {
                size += classicList[i].size
            }
            val ret = ByteArray(size)
            var tempSize = 0
            for (i in classicList.indices) {
                System.arraycopy(
                    classicList[i], 0, ret, tempSize,
                    classicList[i].size
                )
                tempSize += classicList[i].size
            }
            return ret
        }

    override fun add(data: ByteArray): Int {
        classicList.add(data)
        return classicList.size - 1
    }

    override val size: Int
        get() = classicList.size

    /**
     * Retrieve list of byte array
     *
     * @return list of byte generated from input string or byte data
     */
    val list: List<ByteArray>
        get() = classicList

    override fun get(index: Int): ByteArray? {
        return if (index > 0 && index <= classicList.size - 1) {
            classicList[index]
        } else {
            null
        }
    }

    companion object {
        /**
         * number of block to read for each communication with socket
         */
        const val BLOCK_SIZE = 4095
    }
}