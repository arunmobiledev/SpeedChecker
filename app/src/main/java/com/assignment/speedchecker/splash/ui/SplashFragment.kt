/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.splash.ui


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.assignment.speedchecker.BuildConfig
import com.assignment.speedchecker.MainApplication
import com.assignment.speedchecker.R
import com.assignment.speedchecker.databinding.FragmentSplashBinding
import com.assignment.speedchecker.util.CallBackResult
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest

/**
 * A simple [Fragment] subclass.
 */
class SplashFragment : Fragment() {

    private val TAG : String = "SplashFragment"
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.versionName = BuildConfig.VERSION_NAME
        navController = findNavController()
    }

}
