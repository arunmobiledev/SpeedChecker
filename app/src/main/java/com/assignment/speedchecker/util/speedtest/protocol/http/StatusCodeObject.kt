/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http

/**
 * Status return code object builder
 *
 * @author Bertrand Martel
 */
class StatusCodeObject(code: Int, reasonPhrase: String) {
    /** return code value  */
    var code = 0

    /** return code phrase  */
    var reasonPhrase = ""

    /**
     * Display status code object information like in http request according to
     * RFC
     */
    override fun toString(): String {
        return "$code $reasonPhrase"
    }

    /**
     * Return code builder
     *
     * @param code
     * return code value
     * @param reasonPhrase
     * return code phrase
     */
    init {
        this.code = code
        this.reasonPhrase = reasonPhrase
    }
}