/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.constants

import com.assignment.speedchecker.util.speedtest.protocol.http.StatusCodeObject

/**
 * Status code list
 *
 * @author Bertrand Martel
 */
object StatusCodeList {
    val CONTINUE = StatusCodeObject(100, "Continue")
    val SWITCHING_PROTOCOL = StatusCodeObject(101, "Switching Protocols")
    val OK = StatusCodeObject(200, "OK")
    val CREATED = StatusCodeObject(201, "Created")
    val ACCEPTED = StatusCodeObject(202, "Accepted")
    val NON_AUTHORITATIVE_INFORMATION = StatusCodeObject(203, "Non-Authoritative Information")
    val NO_CONTENT = StatusCodeObject(204, "No Content")
    val RESET_CONTENT = StatusCodeObject(205, "Reset Content")
    val PARTIAL_CONTENT = StatusCodeObject(206, "Partial Content")
    val MULTIPLE_CHOICES = StatusCodeObject(300, "Multiple Choices")
    val MOVED_PERMANENTLY = StatusCodeObject(301, "Moved Permanently")
    val FOUND = StatusCodeObject(302, "Found")
    val SEE_OTHER = StatusCodeObject(303, "See Other")
    val NOT_MODIFIED = StatusCodeObject(304, "Not Modified")
    val USE_PROXY = StatusCodeObject(305, "Use Proxy")
    val TEMPORARY_REDIRECT = StatusCodeObject(307, "Temporary Redirect")
    val BAD_REQUEST = StatusCodeObject(400, "Bad Request")
    val UNAUTHORIZED = StatusCodeObject(401, "Unauthorized")
    val PAYMENT_REQUIRED = StatusCodeObject(402, "Payment Required")
    val FORBIDDEN = StatusCodeObject(403, "Forbidden")
    val NOT_FOUND = StatusCodeObject(404, "Not Found")
    val METHOD_NOT_ALLOWED = StatusCodeObject(405, "Method Not Allowed")
    val NOT_ACCEPTABLE = StatusCodeObject(406, "Not Acceptable")
    val PROXY_AUTHENTICATION_REQUIRED = StatusCodeObject(407, "Proxy Authentication Required")
    val REQUEST_TIME_OUT = StatusCodeObject(408, "Request Time-out")
    val CONFLICT = StatusCodeObject(409, "Conflict")
    val GONE = StatusCodeObject(410, "Gone")
    val LENGTH_REQUIRED = StatusCodeObject(411, "Length Required")
    val PRECONDITION_FAILED = StatusCodeObject(412, "Precondition Failed")
    val REQUEST_ENTITY_TOO_LARGE = StatusCodeObject(413, "Request Entity Too Large")
    val REQUEST_URI_TOO_LARGE = StatusCodeObject(414, "Request-URI Too Large")
    val UNSUPPORTED_MEDIA_TYPE = StatusCodeObject(415, "Unsupported Media Type")
    val REQUESTED_RANGE_NOT_SATISFIABLE = StatusCodeObject(416, "Requested range not satisfiable")
    val EXPECTATION_FAILED = StatusCodeObject(417, "Expectation Failed")
    val INTERNAL_SERVER_ERROR = StatusCodeObject(500, "Internal Server Error")
    val NOT_IMPLEMENTED = StatusCodeObject(501, "Not Implemented")
    val BAD_GATEWAY = StatusCodeObject(502, "Bad Gateway")
    val SERVICE_UNAVAILABLE = StatusCodeObject(503, "Service Unavailable")
    val GATEWAY_TIME_OUT = StatusCodeObject(504, "Gateway Time-out")
    val HTTP_VERSION_NOT_SUPPORTED = StatusCodeObject(505, "HTTP Version not supported")
}