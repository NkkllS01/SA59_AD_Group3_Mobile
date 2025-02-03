package com.example.singnature.ExploreMenu.Achievement.Leaderboard

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("leaderboard")  // Adjust endpoint if needed
    suspend fun getLeaderboard(): Response<List<PlayerRank>>
}
