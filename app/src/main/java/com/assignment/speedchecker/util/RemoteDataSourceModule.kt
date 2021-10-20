package com.assignment.speedchecker.util

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.assignment.speedchecker.util.DatasourceProperties.SERVER_URL
import com.prollery.business.account.repo.AccountDataService
import com.prollery.business.countries.repo.CountryDataService
import com.prollery.business.game.repo.GameDataService
import com.prollery.business.player.repo.PlayerDataService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val remoteDatasourceModule = module {
    single { createOkHttpClient() }
    single<PlayerDataService> {
        createWebService(
            get(),
            SERVER_URL
        )
    }
    single<GameDataService> {
        createWebService(
            get(),
            SERVER_URL
        )
    }
    single<AccountDataService> {
        createWebService(
            get(),
            SERVER_URL
        )
    }
    single<CountryDataService> {
        createWebService(
            get(),
            SERVER_URL
        )
    }
}

object DatasourceProperties {
    const val SERVER_URL = "http://192.168.1.6:8093/business/game/"
}

fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
        .connectTimeout(30L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .writeTimeout(30L, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor).build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory()).build()
    return retrofit.create(T::class.java)
}