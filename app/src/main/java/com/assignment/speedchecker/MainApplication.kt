/*
 * Copyright (c) 2021. Arun Raju. All rights reserved
 */

package com.assignment.speedchecker;

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.assignment.speedchecker.checker.repo.SpeedInfoDataService
import com.assignment.speedchecker.checker.viewmodel.SpeedInfoListViewModel
import com.assignment.speedchecker.checker.viewmodel.SpeedInfoViewModel
import com.assignment.speedchecker.util.AppData
import com.assignment.speedchecker.util.AppUtil
import com.assignment.speedchecker.util.CustomLoader
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

/**
 * Main Application
 */
class MainApplication : Application() {


    companion object {
        private var instance: MainApplication? = null
        private var activity: AppCompatActivity? = null
        fun getContext(): Context? {
            return instance
        }
        fun getActivity(): AppCompatActivity? {
            return activity
        }
        fun setActivity(activity: AppCompatActivity?) {
            this.activity = activity
        }
    }

    override fun onCreate() {
        super.onCreate()

        instance = this


        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // start Koin context
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MainApplication)
            modules(listOf(localDatasourceModule, appUtilModule, appDataModule, preConfigurationModules))
        }

    }
}

val preConfigurationModules = module {
    viewModel { SpeedInfoViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SpeedInfoListViewModel(get(), get(), get(), get(), get(), get(), get()) }
}

val appDataModule = module {
    single {
        AppData()
    }
}

val localDatasourceModule = module {
    single { SpeedInfoDataService() }
}

val appUtilModule = module {
    single {
        AppUtil(androidContext())
    }
    single {
        CustomLoader
    }
    single {
        Gson()
    }
    single {
        FirebaseDatabase.getInstance()
    }
}