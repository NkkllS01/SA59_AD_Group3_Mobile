package com.example.singnature.Network

import retrofit2.Call
import com.example.singnature.WildlifeMenu.Species
import retrofit2.http.GET
import retrofit2.http.Query

interface SpeciesApiService {
    @GET("species/search")
    fun searchSpeciesByKeyword(@Query("keyword") keyword: String): Call<List<Species>>
}

val speciesApiService: SpeciesApiService = ApiClient.instance.create(SpeciesApiService::class.java)