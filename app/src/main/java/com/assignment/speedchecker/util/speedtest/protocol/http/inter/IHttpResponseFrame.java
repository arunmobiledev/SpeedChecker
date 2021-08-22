/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.inter;

import java.util.HashMap;

import com.assignment.speedchecker.util.speedtest.protocol.http.HttpVersion;
import com.assignment.speedchecker.util.speedtest.protocol.http.StatusCodeObject;

/**
 * 
 * Http response frame interface
 * 
 * @author Bertrand Martel
 * 
 */
public interface IHttpResponseFrame {

	/** return code for response frame */
	public StatusCodeObject getReturnCode();

	/** http version for response frame */
	public HttpVersion getHttpVersion();

	/** headers for response frame */
	public HashMap<String, String> getHeaders();
}
