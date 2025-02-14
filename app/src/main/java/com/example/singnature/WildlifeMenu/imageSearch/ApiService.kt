package com.example.singnature.WildlifeMenu.imageSearch

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
//     @POST("api/Image/predict") // cloud prototype
    @POST("api/ImageApi/predict") // actual in SingNature Web app
    fun uploadImage(@Part file : MultipartBody.Part) : Call<ClassificationResponse>
}