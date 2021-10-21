/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.assignment.speedchecker.R
import com.assignment.speedchecker.databinding.DialogCustomBinding
import com.assignment.speedchecker.util.progressloading.AVLoadingIndicatorView
import java.text.CharacterIterator
import java.text.SimpleDateFormat
import java.text.StringCharacterIterator
import java.util.*

interface CallBackResult {
    fun onCall(result : String)
}

class AppUtil(private var context: Context) {

    fun showCustomAlertWithAction(activity: Activity?, drawable: Int, msg: String, btnText: String, callBackResult: CallBackResult) : CustomAlertWithAction = CustomAlertWithAction(activity, drawable, msg, btnText, callBackResult)

    fun showToast(msg : String) {
        val toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0,0)
        toast.show()
    }

    fun hideKeyboard(activity: FragmentActivity) {
        val imm: InputMethodManager =  activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isOnline(context: Context): Boolean {
        val isConnected: Boolean
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities)
            ?: return false
        isConnected = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return isConnected
    }

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    @SuppressLint("SimpleDateFormat")
    @Suppress("unused")
    fun getDate(milliSeconds: Long, dateFormat: String?): String { // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    @SuppressLint("DefaultLocale")
    fun humanReadableByteCountSI(bytes: Long): String {
        var resultBytes = bytes
        if (-1000 < resultBytes && resultBytes < 1000) {
            return "$resultBytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (resultBytes <= -999950 || resultBytes >= 999950) {
            resultBytes /= 1000
            ci.next()
        }
        return String.format("%.1f %cbps", resultBytes / 1000.0, ci.current().uppercaseChar())
    }

}

class CustomAlertWithAction(activity: Activity?) : AlertDialog(activity!!) {

    private lateinit var binding : DialogCustomBinding
    private var alertDialog : AlertDialog? = null

    constructor(activity: Activity?, drawable : Int, msg : String, btnText : String, callBackResult: CallBackResult) : this(activity) {
        val viewGroup = findViewById<ViewGroup>(R.id.content)
        val inflater : LayoutInflater = context.getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.bind(inflater.inflate(R.layout.dialog_custom, viewGroup, false))!!
        binding.imgMsg.setImageResource(drawable)
        binding.lblMsg.text = msg
        binding.btnOk.text = btnText
        val dialogView: View = binding.root
        val builder = Builder(context)
        builder.setView(dialogView)
        alertDialog = builder.setCancelable(false).create()
        binding.btnOk.setSafeOnClickListener {
            dismiss()
            callBackResult.onCall("")
        }
    }

    override fun show() {
        super.show()
        alertDialog?.show()
    }

    override fun dismiss() {
        super.dismiss()
        alertDialog?.dismiss()
    }

}

object CustomLoader {
    private const val LOADER_SIZE_SCALE = 8
    private const val LOADER_OFFSET_SCALE = 10
    private val LOADERS = ArrayList<AppCompatDialog?>()


    fun showLoading(context: Context?) {
        val dialog = AppCompatDialog(context, R.style.dialog)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            dialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }
        val avLoadingIndicatorView = AVLoadingIndicatorView(context)
        avLoadingIndicatorView.setBackgroundResource(android.R.color.transparent)
        dialog.setContentView(avLoadingIndicatorView)
        val deviceWidth: Int = DimenUtil.screenWidth
        val deviceHeight: Int = DimenUtil.screenHeight
        val dialogWindow = dialog.window
        val lp = dialogWindow!!.attributes
        lp.width = deviceWidth / LOADER_SIZE_SCALE
        lp.height = deviceHeight / LOADER_SIZE_SCALE
        lp.height = lp.height + deviceHeight / LOADER_OFFSET_SCALE
        lp.gravity = Gravity.CENTER
        LOADERS.add(dialog)
        dialog.show()
    }


    fun stopLoading() {
        for (dialog in LOADERS) {
            if (dialog != null) {
                if (dialog.isShowing) {
                    dialog.cancel()
                }
            }
        }
    }

}







