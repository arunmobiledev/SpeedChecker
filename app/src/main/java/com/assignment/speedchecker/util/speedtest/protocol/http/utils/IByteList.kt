/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.utils

/**
 * Custom interface for list for stocking data in different byte[] array for
 * buffer issue resolution
 *
 * @author Bertrand Martel
 */
interface IByteList {
    /** return index of element added in the list  */
    fun add(data: ByteArray): Int

    /** return all byte array of list  */
    val bytes: ByteArray

    /** return list size  */
    val size: Int

    /** return list element by index  */
    operator fun get(index: Int): ByteArray?
}