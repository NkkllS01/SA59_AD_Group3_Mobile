package com.example.singnature.Network

import retrofit2.Call
import com.example.singnature.WildlifeMenu.Sightings
import retrofit2.http.GET

interface SightingsApiService {
    @GET("sightings")
    fun getActiveSightings(): Call<List<Sightings>>
}

val sightingsApiService: SightingsApiService = ApiClient.instance.create(SightingsApiService::class.java)