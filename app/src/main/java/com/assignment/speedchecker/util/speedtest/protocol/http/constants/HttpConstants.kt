/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.constants

/**
 * **Define HTTP constants**
 *
 * @author Bertrand Martel
 */
object HttpConstants {
    /**
     * HTTP line delimiter
     */
    const val HEADER_DELEMITER = "\r\n"

    /**
     * HTTP header delimiter
     */
    const val HEADER_TERMINATOR = "\r\n" + "\r\n"

    /**
     * HTTP header delimiter.
     */
    const val HEADER_VALUE_DELIMITER = ": "

    /**
     * NOT FOUND RESPONSE String definition
     */
    const val NOT_FOUND = ("HTTP/1.1 404 Not Found\r\n"
            + "Content-Length: 22\r\n" + "Content-Type: text/html\r\n\r\n"
            + "<h1>404 Not Found</h1>")

    /**
     * SERVER ERROR RESPONSE String definition
     */
    const val BAD_REQUEST_ERROR = ("HTTP/1.1 400 Bad Request\r\n"
            + "Content-Length: 53\r\n" + "Content-Type: text/html\r\n\r\n"
            + "<h1>400 Bad Request</h1><p>Malformed HTTP request</p>")

    /**
     * SERVER ERROR RESPONSE String definition
     */
    const val INTERNAL_SERVER_ERROR = ("HTTP/1.1 500 Internal Server Error\r\n"
            + "Content-Length: 34\r\n"
            + "Content-Type: text/html\r\n\r\n"
            + "<h1>500 Internal Server Error</h1>")

    /**
     * OK RESPONSE String definition
     */
    const val OK = ("HTTP/1.1 200 OK\r\n" + "Content-Length: 18\r\n"
            + "Content-Type: text/html\r\n\r\n" + "<h1>200 Found</h1>")

    /**
     * REDIRECTION
     */
    const val REDIRECTION = ("HTTP/1.1 301 Moved Permanently\r\n"
            + "Content-Length: 22\r\n" + "Content-Type: text/html\r\n\r\n"
            + "<h1>301     Found</h1>")
}