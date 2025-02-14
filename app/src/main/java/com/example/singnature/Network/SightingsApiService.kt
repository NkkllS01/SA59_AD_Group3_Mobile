package com.example.singnature.Network

import retrofit2.Call
import com.example.singnature.WildlifeMenu.Sightings
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface SightingsApiService {
    @GET("sightings")
    fun getActiveSightings(): Call<List<Sightings>>

    @GET("sightings/search/{keyword}")
    fun searchSightingsByKeyword(@Path(value = "keyword", encoded = true) keyword: String): Call<List<Sightings>>

    @GET("sightings/{id}")
    fun getSightingById(@Path("id") id: Int): Call<Sightings>

    @Multipart
    @POST("sightings")
    fun createSighting(@Part("sighting") sighting: RequestBody,
    @Part file: MultipartBody.Part
    ):Call<Sightings>
}

val sightingsApiService: SightingsApiService = ApiClient.instance.create(SightingsApiService::class.java)