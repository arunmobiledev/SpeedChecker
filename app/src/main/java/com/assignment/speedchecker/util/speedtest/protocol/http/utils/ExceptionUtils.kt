/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.utils

import kotlin.Throws
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import java.lang.Exception
import java.lang.StringBuilder

object ExceptionUtils {
    /**
     * Retrieve exception stack message
     *
     * @param e
     * Exception e
     * @return excpetion message
     */
    fun getExceptionMessage(e: Exception): String {
        val sb = StringBuilder()
        e.printStackTrace(PrintStream(object : OutputStream() {
            @Throws(IOException::class)
            override fun write(b: Int) {
                sb.append(b.toChar())
            }
        }))
        return sb.toString()
    }
}