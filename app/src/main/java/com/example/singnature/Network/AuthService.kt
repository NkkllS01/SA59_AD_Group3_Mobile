package com.example.singnature.Network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthService {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @PUT("auth/update-profile")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<Void>

}
