package com.example.singnature.ExploreMenu.Achievement.Leaderboard

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class LeaderboardViewModel(private val context: Context) : ViewModel() {
    private val _leaderboard = MutableLiveData<List<PlayerRank>>()
    val leaderboard: LiveData<List<PlayerRank>> get() = _leaderboard

    private val leaderboardRepository = LeaderboardRepository(context)

    fun loadLeaderboard() {
        // Fetch the leaderboard data asynchronously
        Thread {
            val leaderboard = leaderboardRepository.getRankedLeaderboard()
            _leaderboard.postValue(leaderboard) // Update LiveData on the main thread
        }.start()
    }
}