/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.inter;

import java.util.HashMap;

import com.assignment.speedchecker.util.speedtest.protocol.http.HttpReader;
import com.assignment.speedchecker.util.speedtest.protocol.http.HttpVersion;
import com.assignment.speedchecker.util.speedtest.protocol.http.utils.IByteList;

/**
 * Interface for http request frame template
 * 
 * @author Bertrand Martel
 * 
 */
public interface IHttpFrame {

	/** http request version */
	public HttpVersion getHttpVersion();

	/**
	 * Retrieve reason phrase of http frame (empty string if not exists)
	 *
	 */
	public String getReasonPhrase();

	/**
	 * Retrieve status code of http frame (-1 if not exists)
	 *
	 */
	public int getStatusCode();

	/**
	 * http reader permitting to read and parse http frame on inputstream
	 * channel
	 */
	public HttpReader getReader();

	/** http Host name */
	public String getHost();

	/** list of http headers */
	public HashMap<String, String> getHeaders();

	/** request method */
	public String getMethod();

	/** request uri */
	public String getUri();

	/** queryString for http request */
	public String getQueryString();

	/** request body content */
	public IByteList getBody();

	/** identify the frame as a request frame */
	public boolean isHttpRequestFrame();

	/** identify the frame as a response frame */
	public boolean isHttpResponseFrame();

	/** identify if the frame has chunked body or nto */
	public boolean isChunked();
}
