package com.example.singnature.Network

import com.example.singnature.WarningMenu.Warning
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WarningApiService {
    @GET("warnings")
    fun getAllWarnings(): Call<List<Warning>>

    @GET("warnings/{warningId")
    fun getWarningById(@Path("warningId") warningId: Int): Call<Warning>
}

val warningApiService: WarningApiService = ApiClient.instance.create(WarningApiService::class.java)