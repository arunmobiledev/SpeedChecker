/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.checker.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
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
import android.util.Log
import com.assignment.speedchecker.util.AppUtil
import org.koin.android.ext.android.inject
import androidx.fragment.app.FragmentTransaction
import com.assignment.speedchecker.checker.viewmodel.SpeedInfoListViewModel
import com.assignment.speedchecker.databinding.FragmentSpeedInfoListBinding
import java.lang.Exception
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment.speedchecker.checker.adapter.HistoryAdapter
import com.assignment.speedchecker.util.searchview.PrSearchView.DrawableClickListener.DrawablePosition
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.os.Looper
import com.assignment.speedchecker.util.searchview.PrSearchView

/**
 * A simple [SpeedInfoListFragment] subclass.
 */
class SpeedInfoListFragment : Fragment() {

    companion object {
        private const val TAG : String = "SpeedInfoListFragment"
    }

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSpeedInfoListBinding
    private val appUtil: AppUtil by inject()
    private val speedInfoListViewModel: SpeedInfoListViewModel by inject()
    private var lstSpeedInfoModel: ArrayList<SpeedInfoModel> = ArrayList()
    var adapter: HistoryAdapter? = null
    private var searchViewTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_speed_info_list, container, false)
        navController = findNavController()

        return binding.root
    }

    @SuppressLint("HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.appUtil = appUtil

        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                appUtil.hideKeyboard(requireActivity())
                navController.popBackStack()
                true
            } else {
                false
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            appUtil.hideKeyboard(requireActivity())
            navController.popBackStack()
        }

        speedInfoListViewModel.usersInfoSavedSuccess.observe(viewLifecycleOwner, {
            Log.d(TAG, it!!.second.toString())
            lstSpeedInfoModel.clear()
            lstSpeedInfoModel.addAll(it.second)
            initList()
        })

        speedInfoListViewModel.toast.observe(viewLifecycleOwner, {
            Log.d(TAG, it!!.msg)
        })

        speedInfoListViewModel.getUsers()

        binding.searchView.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                return@OnEditorActionListener true
            }
            false
        })

        if (searchViewTextWatcher == null) {
            searchViewTextWatcher = object : TextWatcher {

                override fun beforeTextChanged(charSequence: CharSequence,i: Int,i1: Int,i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {
                    val exe: ExecutorService = Executors.newSingleThreadExecutor()
                    exe.execute(SearchAppListTask(editable.toString()))
                    exe.shutdown()
                }
            }
        }
        binding.searchView.addTextChangedListener(searchViewTextWatcher)
        binding.searchView.setDrawableClickListener(object : PrSearchView.DrawableClickListener {
            override fun onClick(target: DrawablePosition?) {
                if (DrawablePosition.END == target) {
                    binding.searchView.setText("")
                }
            }
        })

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

    private fun initList() {
        // Setting up images grid
        val manager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rvHistory.layoutManager = manager
        binding.rvHistory.itemAnimator = DefaultItemAnimator()
        if (lstSpeedInfoModel.size > 0) {
            binding.rvHistory.visibility = View.VISIBLE
            binding.frmNoItems.visibility = View.GONE
        } else {
            binding.rvHistory.visibility = View.GONE
            binding.frmNoItems.visibility = View.VISIBLE
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        adapter = HistoryAdapter(lstSpeedInfoModel, appUtil)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
        binding.rvHistory.setHasFixedSize(true)
        binding.rvHistory.setItemViewCacheSize(5)
    }

    fun searchHistory(strQuery: String): ArrayList<SpeedInfoModel> {
        return lstSpeedInfoModel.filter { it.mobileNumber?.contains(strQuery) == true } as ArrayList<SpeedInfoModel>
    }

    inner class SearchAppListTask(query: String) : Runnable {
        var query = ""
        var lstspeed: ArrayList<SpeedInfoModel> = ArrayList()

        init {
            this.query = query
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun run() {
            lstspeed.clear()
            lstspeed = searchHistory(query)
            Handler(Looper.getMainLooper()).post {
                if (lstspeed.size > 0) {
                    binding.rvHistory.visibility = View.VISIBLE
                    binding.frmNoItems.visibility = View.GONE
                } else {
                    binding.rvHistory.visibility = View.GONE
                    binding.frmNoItems.visibility = View.VISIBLE
                }
                adapter?.setArrayList(lstspeed)
                adapter?.notifyDataSetChanged()
            }
        }
    }

}
