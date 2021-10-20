package com.assignment.speedchecker.checker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.assignment.speedchecker.MainApplication
import com.assignment.speedchecker.R
import com.assignment.speedchecker.checker.repo.PlayerDataService
import com.assignment.speedchecker.util.SingleLiveEvent
import com.assignment.speedchecker.util.ToastModel
import com.google.android.gms.common.api.Response
import com.prollery.business.MainApplication
import com.prollery.business.R
import com.prollery.business.account.model.Account
import com.prollery.business.player.model.Player
import com.prollery.business.player.model.PlayerResponse
import com.prollery.business.player.repo.PlayerDataService
import com.prollery.business.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response


class PlayerViewmodel(private val playerDataService: PlayerDataService, application: Application, val appUtil: AppUtil, val customLoader: CustomLoader) : AndroidViewModel(application) {

    private val TAG : String = "PlayerViewmodel"
    val toast : SingleLiveEvent<ToastModel>  = SingleLiveEvent()
    val playersFetchedSuccessfully : SingleLiveEvent<Pair<Boolean, ArrayList<Player>>>  = SingleLiveEvent()

    fun getPlayers(loading: Boolean) {
        if (!appUtil.isNetworkAvailable()) {
            toast.value = ToastModel(
                R.drawable.ic_error,
                getApplication<MainApplication>().getString(R.string.internet_error)
            )
            return
        }
        if (loading) {
            customLoader.showLoading(getApplication<MainApplication>().applicationContext)
        }
        CoroutineScope(Dispatchers.IO).launch {
            var response: Response<PlayerResponse>? = null
            try {
                response = playerDataService.getPlayers()
            } catch (e: Exception) {
                Log.d(TAG, "Exception ${e.message}")
                customLoader.stopLoading()
            }
            try {
                if (response != null) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Success: ${response.body().toString()}")
//                            toast.value = ToastModel(R.drawable.ic_info, response.body()!!.message)
                        withContext(Dispatchers.Main) { playersFetchedSuccessfully.value = true to response.body()!!.players!! }
                    } else {
                        Log.d(TAG, "Error: ${response.code()}")
                        val apiError = ApiError(response.errorBody()!!.string())
                        withContext(Dispatchers.Main) { toast.value = ToastModel(R.drawable.ic_error, apiError.message()) }
                    }
                    withContext(Dispatchers.Main) { customLoader.stopLoading() }
                } else {
                    withContext(Dispatchers.Main) {
                        toast.value = ToastModel(R.drawable.ic_error,getApplication<MainApplication>().getString(R.string.something_went_wrong))
                        customLoader.stopLoading()
                    }
                }
            } catch (e: HttpException) {
                Log.d(TAG, "Exception ${e.message}")
                withContext(Dispatchers.Main) {
                    toast.value = ToastModel(R.drawable.ic_error,getApplication<MainApplication>().getString(R.string.something_went_wrong))
                    customLoader.stopLoading()
                }
            } catch (e: Throwable) {
                Log.d(TAG, "Exception ${e.message}")
                withContext(Dispatchers.Main) {
                    toast.value = ToastModel(R.drawable.ic_error,getApplication<MainApplication>().getString(R.string.something_went_wrong))
                    customLoader.stopLoading()
                }
            }
        }
    }

    fun getAccountsForSpinner(lstAccount : ArrayList<Account>, lstPlayer : ArrayList<Player>) : ArrayList<String> {
        val lstSpinnerAccounts = ArrayList<String>()
        lstAccount.forEach { account ->
            lstPlayer.forEach { player ->
                if(account.playerId == player.playerId)
                    lstSpinnerAccounts.add("${player.userName} (${account.accNumber})")
            }
        }

        lstSpinnerAccounts.reverse()
        return lstSpinnerAccounts
    }


}