/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http

/**
 * Http version builder and parser
 *
 * @author Bertrand Martel
 */
class HttpVersion {
    /** version first digit (before ".")  */
    var versionDigit1 = 0

    /** version second digit  */
    var versionDigit2 = 0

    /**
     * Http version builder according to two digit parameters (RFC)
     *
     * @param versionDigit1
     * @param versionDigit2
     */
    constructor(versionDigit1: Int, versionDigit2: Int) {
        this.versionDigit1 = versionDigit1
        this.versionDigit2 = versionDigit2
    }

    /**
     * Parser for http version
     *
     * @param stringToParse
     * http request to parse
     */
    constructor(stringToParse: String) {
        if (stringToParse.startsWith("HTTP/")
            && stringToParse.length > "HTTP/".length + 2
        ) {
            val version1 = stringToParse.substring(
                stringToParse.indexOf('/') + 1,
                stringToParse.indexOf('/') + 2
            ).toInt()
            val version2 = stringToParse.substring(
                stringToParse.indexOf('/') + 3,
                stringToParse.indexOf('/') + 4
            ).toInt()
            versionDigit1 = version1
            versionDigit2 = version2
        }
    }

    /**
     * format http version according to RFC
     */
    override fun toString(): String {
        return "HTTP/" + versionDigit1 + "." + versionDigit2
    }
}