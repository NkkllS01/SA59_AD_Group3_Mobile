package com.example.singnature.Network

import retrofit2.Call
import com.example.singnature.WildlifeMenu.Species
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpeciesApiService {
    @GET("species/search/{keyword}")
    fun searchSpeciesByKeyword(@Path(value = "keyword", encoded = true) keyword: String): Call<List<Species>>

    @GET("species/{specieId}")
    fun getSpeciesById(@Path("specieId") specieId: Int): Call<Species>

    @GET("species/category/{categoryId}")
    fun getSpeciesByCategory(@Path("categoryId") categoryId: Int): Call<List<Species>>
    }


val speciesApiService: SpeciesApiService = ApiClient.instance.create(SpeciesApiService::class.java)