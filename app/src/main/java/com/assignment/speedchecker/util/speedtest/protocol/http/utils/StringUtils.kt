/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.utils

/**
 * Some functions to deal with string
 *
 * @author Bertrand Martel
 */
object StringUtils {
    /**
     * from
     * http://stackoverflow.com/questions/237159/whats-the-best-way-to-check
     * -to-see-if-a-string-represents-an-integer-in-java
     *
     *
     * Efficient way to test if string is a valid integer
     *
     * @param str
     * @return
     */
    fun isInteger(str: String?): Boolean {
        if (str == null) {
            return false
        }
        val length = str.length
        if (length == 0) {
            return false
        }
        var i = 0
        if (str[0] == '-') {
            if (length == 1) {
                return false
            }
            i = 1
        }
        while (i < length) {
            val c = str[i]
            if (c <= '/' || c >= ':') {
                return false
            }
            i++
        }
        return true
    }
}