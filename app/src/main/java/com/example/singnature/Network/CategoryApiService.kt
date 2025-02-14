package com.example.singnature.Network

import com.example.singnature.WildlifeMenu.SpeciesCategory
import retrofit2.Call
import retrofit2.http.GET

interface CategoryApiService {
    @GET("api/categories")
    fun getCategories(): Call<List<SpeciesCategory>>
}
