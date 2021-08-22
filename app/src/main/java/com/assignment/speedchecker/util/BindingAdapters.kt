/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener


@BindingAdapter("onEditorActionListener")
fun bindOnEditorActionListener(editText: EditText, editorActionListener: TextView.OnEditorActionListener) {
    editText.setOnEditorActionListener(editorActionListener)
}

@BindingAdapter(value = ["selectedValue", "selectedValueAttrChanged"], requireAll = false)
fun bindSpinnerData(
    pAppCompatSpinner: AppCompatSpinner,
    newSelectedValue: String?,
    newTextAttrChanged: InverseBindingListener
) {
    pAppCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            newTextAttrChanged.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
    if (newSelectedValue != null) {
        val pos = (pAppCompatSpinner.adapter as ArrayAdapter<String?>).getPosition(
            newSelectedValue
        )
        pAppCompatSpinner.setSelection(pos, true)
    }
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun captureSelectedValue(pAppCompatSpinner: AppCompatSpinner): String? {
    return pAppCompatSpinner.selectedItem as String
}