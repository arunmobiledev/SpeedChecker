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
            val resources: Resources = MainApplication.getContext()!!.getResources()
            val dm: DisplayMetrics = resources.getDisplayMetrics()
            return dm.widthPixels
        }

    val screenHeight: Int
        get() {
            val resources: Resources = MainApplication.getContext()!!.getResources()
            val dm: DisplayMetrics = resources.getDisplayMetrics()
            return dm.heightPixels
        }
}