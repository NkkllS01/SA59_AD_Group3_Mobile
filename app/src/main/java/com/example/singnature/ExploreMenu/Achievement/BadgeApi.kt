package com.example.singnature.ExploreMenu.Achievement

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

    interface BadgeApi {

        @GET("badges")
        suspend fun getBadges(): List<Badge>

        @POST("badges")
        suspend fun addBadge(@Body badge: Badge): Badge
    }

