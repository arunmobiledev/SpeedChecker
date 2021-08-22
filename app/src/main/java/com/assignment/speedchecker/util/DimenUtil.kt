/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util

import android.content.res.Resources
import android.util.DisplayMetrics
import com.assignment.speedchecker.MainApplication

object DimenUtil {
    val screenWidth: Int
        get() {
            val resources: Resources = MainApplication.getContext()!!.resources
            val dm: DisplayMetrics = resources.displayMetrics
            return dm.widthPixels
        }

    val screenHeight: Int
        get() {
            val resources: Resources = MainApplication.getContext()!!.resources
            val dm: DisplayMetrics = resources.displayMetrics
            return dm.heightPixels
        }
}