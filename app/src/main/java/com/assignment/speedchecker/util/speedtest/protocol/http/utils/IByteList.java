/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.protocol.http.utils;

/**
 * Custom interface for list for stocking data in different byte[] array for
 * buffer issue resolution
 * 
 * @author Bertrand Martel
 * 
 */
public interface IByteList {

	/** return index of element added in the list */
	public int add(byte[] data);

	/** return all byte array of list */
	public byte[] getBytes();

	/** return list size */
	public int getSize();

	/** return list element by index */
	public byte[] get(int index);
}
