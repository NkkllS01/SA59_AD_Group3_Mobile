package com.example.singnature.Network

import com.example.singnature.ExploreMenu.Park
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ParkApiService {
    @GET("parks")
    fun getAllParks(): Call<List<Park>>

    @GET("parks/{parkId}")
    fun getParkById(@Path("parkId") parkId: Int): Call<Park>
}

val parkApiService: ParkApiService = ApiClient.instance.create(ParkApiService::class.java)