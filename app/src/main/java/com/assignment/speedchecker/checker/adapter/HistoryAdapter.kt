/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.checker.adapter

import com.assignment.speedchecker.checker.model.SpeedInfoModel
import com.assignment.speedchecker.util.AppUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import com.assignment.speedchecker.R
import com.assignment.speedchecker.databinding.ItemHistoryBinding
import java.util.ArrayList

/**
 * Created by arun on 11/7/16.
 */
class HistoryAdapter(var lstHistoryBean: ArrayList<SpeedInfoModel>, var appUtil: AppUtil) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemHistoryBinding: ItemHistoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_history,
            parent,
            false
        )
        return ViewHolder(itemHistoryBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemHistoryBinding = holder.itemHistoryBinding
        val data = lstHistoryBean[position]
        itemHistoryBinding.appUtil = appUtil
        itemHistoryBinding.speedInfoModel = data
    }

    override fun getItemCount(): Int {
        return lstHistoryBean.size
    }

    class ViewHolder(var itemHistoryBinding: ItemHistoryBinding) : RecyclerView.ViewHolder(
        itemHistoryBinding.root
    )

    fun setArrayList(lstHistoryBean: ArrayList<SpeedInfoModel>) {
        this.lstHistoryBean = lstHistoryBean
    }

}