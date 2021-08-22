/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http

import kotlin.Throws
import java.io.IOException
import java.io.InputStream
import java.net.SocketException

/**
 * Http reader : read http frame separated by \r\n from inputstream
 *
 * @author Bertrand Martel
 */
class HttpReader {
    /**
     *
     *
     * Read a character sequence from the inpustream. Return the character
     * sequence as String object and null if no bytes can be read from the
     * inputstream. Data is stored in string buffer object and retruned by this
     * function
     *
     *
     * @return The next line in the input stream or null for EOF
     * @throws IOException
     * on I/O error
     * @param in
     * socket inputStream
     * @return parsed line terminated by \r\n
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readLine(`in`: InputStream?): String? {
        return try {
            var buffer = ""
            var bytesRead = 0
            var i = 0
            var stop = false
            if (`in` != null) {
                while (stop == false && `in`.read().also { i = it } >= 0) {
                    bytesRead++
                    if ('\n' == i.toChar()) {
                        stop = true
                    } else if ('\r' != i.toChar()) {
                        buffer = buffer + i.toChar()
                    }
                }
            }
            if (bytesRead == 0) {
                null
            } else buffer
        } catch (e: SocketException) {
            null
        }
    }
}