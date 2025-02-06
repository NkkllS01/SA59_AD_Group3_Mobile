package com.example.singnature.Network

import retrofit2.Call
import com.example.singnature.WildlifeMenu.Sightings
import retrofit2.http.GET
import retrofit2.http.Query

interface SightingsApiService {
    @GET("sightings")
    fun getActiveSightings(): Call<List<Sightings>>

    @GET("sightings/search")
    fun searchSightingsByKeyword(@Query("keyword") keyword: String): Call<List<Sightings>>
}

val sightingsApiService: SightingsApiService = ApiClient.instance.create(SightingsApiService::class.java)