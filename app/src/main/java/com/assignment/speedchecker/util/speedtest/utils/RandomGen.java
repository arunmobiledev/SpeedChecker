/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util.speedtest.utils;

import com.assignment.speedchecker.util.speedtest.SpeedTestConst;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * Generate Random byte array, file for randomly generated uploaded file.
 *
 * @author Bertrand Martel
 */
public class RandomGen {

    /**
     * Random object.
     */
    private final Random mRandom = new Random();

    /**
     * Random generated file.
     */
    private File mFile;

    /**
     * Generate random byte array.
     *
     * @param length number of bytes to be generated
     * @return random byte array
     */
    public byte[] generateRandomArray(final int length) {

        final byte[] buffer = new byte[length];

        final int iter = length / SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK;
        final int remain = length % SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK;

        for (int i = 0; i < iter; i++) {
            final byte[] random = new byte[SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK];
            mRandom.nextBytes(random);
            System.arraycopy(random, 0, buffer, i * SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK, SpeedTestConst
                    .UPLOAD_FILE_WRITE_CHUNK);
        }
        if (remain > 0) {
            final byte[] random = new byte[remain];
            mRandom.nextBytes(random);
            System.arraycopy(random, 0, buffer, iter * SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK, remain);
        }
        return buffer;
    }

    /**
     * Generate random file.
     *
     * @param length number of bytes to be generated
     * @return file with random content
     */
    public RandomAccessFile generateRandomFile(final int length) throws IOException {

        mFile = File.createTempFile(SpeedTestConst.UPLOAD_TEMP_FILE_NAME,
                SpeedTestConst.UPLOAD_TEMP_FILE_EXTENSION);

        final RandomAccessFile randomFile = new RandomAccessFile(mFile.getAbsolutePath(), "rw");
        randomFile.setLength(length);

        final int iter = length / SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK;
        final int remain = length % SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK;

        for (int i = 0; i < iter; i++) {
            final byte[] random = new byte[SpeedTestConst.UPLOAD_FILE_WRITE_CHUNK];
            mRandom.nextBytes(random);
            randomFile.write(random);
        }
        if (remain > 0) {
            final byte[] random = new byte[remain];
            mRandom.nextBytes(random);
            randomFile.write(random);
        }

        return randomFile;
    }

    /**
     * Delete random file.
     */
    public void deleteFile() {
        if (mFile != null) {
            mFile.delete();
        }
    }
}
