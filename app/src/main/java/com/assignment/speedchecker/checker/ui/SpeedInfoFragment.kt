/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.checker.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.assignment.speedchecker.R
import com.assignment.speedchecker.checker.model.SpeedInfoModel
import androidx.core.content.ContextCompat
import com.assignment.speedchecker.databinding.FragmentSpeedInfoBinding
import com.assignment.speedchecker.util.AppUtil
import org.koin.android.ext.android.inject
import androidx.fragment.app.FragmentTransaction
import com.assignment.speedchecker.checker.viewmodel.SpeedInfoViewModel
import com.assignment.speedchecker.util.AppData
import java.lang.Exception

/**
 * A simple [SpeedInfoFragment] subclass.
 */
class SpeedInfoFragment : Fragment() {

    companion object {
        private const val TAG : String = "SpeedInfoFragment"
    }

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSpeedInfoBinding
    private val appUtil: AppUtil by inject()
    private val appData: AppData by inject()
    private val speedInfoViewModel: SpeedInfoViewModel by inject()
    private var currentSpeedInfoModel: SpeedInfoModel? = null
    private var initiallyUpdated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_speed_info, container, false)
        navController = findNavController()

        return binding.root
    }


    @SuppressLint("HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.appUtil = appUtil

        if(currentSpeedInfoModel == null) {
            currentSpeedInfoModel = SpeedInfoModel(appData.getString(AppData.MOBILE_NUMBER))
        }
        speedInfoViewModel.speedInfoModel.value = currentSpeedInfoModel
        setData(currentSpeedInfoModel)

        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().finish()
                true
            } else {
                false
            }
        }

        if(!appUtil.isOnline(requireContext())) {
            binding.frmNoInternet?.visibility = View.VISIBLE
            binding.cltInternetAvailable?.visibility = View.GONE
            return
        } else {
            binding.frmNoInternet?.visibility = View.GONE
            binding.cltInternetAvailable?.visibility = View.VISIBLE
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }

        binding.btnReload1?.setOnClickListener {
            updateScreenData()
        }

        binding.btnReload2?.setOnClickListener {
            updateScreenData()
        }

        binding.btnHistory.setOnClickListener {
            navController.navigate(R.id.action_speedInfoFragment_to_speedInfoListFragment)
        }

        binding.btnUpload.setOnClickListener {
            try {
                speedInfoViewModel.updateUser(currentSpeedInfoModel!!)
            } catch(e : Exception) {
                e.printStackTrace()
            }
        }

        speedInfoViewModel.completeListener.observe(viewLifecycleOwner, {
            if(it!!.first == "download") {
                if(!it.second) {
                    binding.lblDownloadSpeed.text = getString(R.string.download_test_failed)
                }
                binding.lblProgress?.text = getString(R.string.upload_test_running)
                binding.lblProgress?.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                speedInfoViewModel.updateUploadSpeedTask()
            }
            if(it.first == "upload") {
                if(!it.second) {
                    binding.lblUploadSpeed.text = getString(R.string.upload_test_failed)
                    binding.lblProgress?.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
                binding.lblProgress?.text = getString(R.string.speed_test_result)
                binding.lblProgress?.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

                binding.btnUpload.setBackgroundResource(R.drawable.circle_button_bg)
                binding.btnUpload.tag = "enabled"
                speedInfoViewModel.speedInfoModel.value?.updateTime()
            }
        })

        speedInfoViewModel.speedInfoModel.observe(viewLifecycleOwner, {
            setData(it)
        })

        speedInfoViewModel.toast.observe(viewLifecycleOwner, {
            it?.msg?.let { it1 -> appUtil.showToast(it1) }
        })

        if(!initiallyUpdated) {
            updateNetSpeed()
            initiallyUpdated = true
        }

    }

    private fun updateNetSpeed() {
        updateScreenData()
        binding.btnUpload.setBackgroundResource(R.drawable.circle_button_bg_disabled)
        binding.btnUpload.tag = "disabled"
    }

    private fun updateScreenData() {
        if(!appUtil.isOnline(requireContext())) {
            binding.frmNoInternet?.visibility = View.VISIBLE
            binding.cltInternetAvailable?.visibility = View.GONE
            return
        } else {
            binding.frmNoInternet?.visibility = View.GONE
            binding.cltInternetAvailable?.visibility = View.VISIBLE
        }
        binding.lblProgress?.text = getString(R.string.download_test_running)
        binding.lblProgress?.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        setData(currentSpeedInfoModel)
        speedInfoViewModel.updateDownloadSpeedTask()
        currentSpeedInfoModel?.updateTime()
    }

    private fun setData(speedInfoModel: SpeedInfoModel?) {
        if(speedInfoModel != null && speedInfoModel.mobileNumber == null) {
            speedInfoModel.mobileNumber = ""
        }
        binding.speedInfoModel = speedInfoModel
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            try {
                val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
                ft.setReorderingAllowed(false)
                ft.detach(this).attach(this).commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
