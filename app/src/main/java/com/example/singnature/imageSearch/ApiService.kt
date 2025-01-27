package com.example.singnature.imageSearch

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST ("api/Image/predict")
    fun uploadImage(@Part file : MultipartBody.Part): Call<ClassificationResponse>

}