/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker.checker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.assignment.speedchecker.MainApplication
import com.assignment.speedchecker.R
import com.assignment.speedchecker.checker.model.SpeedInfoModel
import com.assignment.speedchecker.checker.repo.SpeedInfoDataService
import com.assignment.speedchecker.util.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*

class SpeedInfoListViewModel(private val mFirebaseInstance: FirebaseDatabase, val appUtil: AppUtil, private val speedInfoDataService: SpeedInfoDataService, application: Application, private val gson : Gson, val appData: AppData, private val customLoader: CustomLoader) : AndroidViewModel(application) {

    val toast : SingleLiveEvent<ToastModel>  = SingleLiveEvent()
    var speedInfoModel : MutableLiveData<SpeedInfoModel>  = MutableLiveData()
    val usersInfoSavedSuccess : SingleLiveEvent<Pair<Boolean, ArrayList<SpeedInfoModel>>>  = SingleLiveEvent()

    companion object {
        private const val TAG : String = "SpeedInfoListViewModel"
    }

    fun getUsers() {
        if(MainApplication.getActivity() != null) {
            customLoader.showLoading(MainApplication.getActivity())
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // get reference to 'users' node
                val mFirebaseDatabase = mFirebaseInstance.getReference("users")
                mFirebaseDatabase.limitToFirst(10)
                mFirebaseDatabase.addValueEventListener(object  : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val token: TypeToken<ArrayList<SpeedInfoModel>> = object : TypeToken<ArrayList<SpeedInfoModel>>() {}
                        val lst = ArrayList<SpeedInfoModel>()
                        for (data in snapshot.children) {
                            val speedInfoModel1 = data.getValue(SpeedInfoModel::class.java)
                            speedInfoModel1?.let { lst.add(it) }
                        }
                        Log.d(TAG, "Success: $lst")
                        usersInfoSavedSuccess.value = Pair(true, lst)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG, "Error: $error")
                        toast.value = ToastModel(R.drawable.ic_error, error.message)
                    }
                })
            } catch (e : Exception) {
                Log.d(TAG, e.message!!)
                toast.value = ToastModel(R.drawable.ic_error, getApplication<MainApplication>().getString(R.string.something_went_wrong))
            }  finally {
                customLoader.stopLoading()
            }
        }
    }

}