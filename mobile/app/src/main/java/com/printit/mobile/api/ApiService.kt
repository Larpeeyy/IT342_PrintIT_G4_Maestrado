package com.printit.mobile.api

import com.printit.mobile.model.LoginRequest
import com.printit.mobile.model.RegisterRequest
import com.printit.mobile.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<UserResponse>

    @POST("api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<UserResponse>
}