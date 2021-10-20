package com.assignment.speedchecker.checker.repo

import com.prollery.business.player.model.LoginResponse
import com.prollery.business.player.model.PlayerResponse
import com.prollery.business.player.model.SignUpResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PlayerDataService {

    @POST("signup_player" )
    suspend fun signUpPlayer(@Query("user_name") user_name : String, @Query("password") password : String) : Response<SignUpResponse>

    @POST("login_player" )
    suspend fun loginPlayer(@Query("user_name") user_name : String, @Query("password") password : String) : Response<LoginResponse>

    @GET("get_all_players")
    suspend fun getPlayers() : Response<PlayerResponse>

}