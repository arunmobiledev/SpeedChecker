/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */
package com.assignment.speedchecker.util.speedtest.model

/**
 * Enum for Upload storage Type.
 *
 * @author Bertrand Martel
 */
enum class UploadStorageType {
    /**
     * Default mode, upload file is stored in RAM.
     */
    RAM_STORAGE,

    /**
     * Upload file is stored in a temporary file that will be deleted at the end of upload.
     */
    FILE_STORAGE
}