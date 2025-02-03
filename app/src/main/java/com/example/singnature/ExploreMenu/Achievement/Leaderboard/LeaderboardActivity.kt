package com.example.singnature.ExploreMenu.Achievement.Leaderboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelProvider
import com.example.singnature.databinding.ActivityLeaderboardBinding


class LeaderboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var viewModel: LeaderboardViewModel
    private lateinit var leaderboardRepository: LeaderboardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the leaderboardRepository with context
        leaderboardRepository = LeaderboardRepository(this)

        // Initialize ViewModel using the custom factory
        val factory = LeaderboardViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory).get(LeaderboardViewModel::class.java)

        // Set up RecyclerView with an empty adapter first
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = LeaderboardAdapter(emptyList()) // Set an empty adapter initially ✅

        // Observe the ViewModel data and handle UI updates
        viewModel.leaderboard.observe(this) { leaderboard ->
            val rankedLeaderboard = assignRanks(leaderboard)
            (binding.recyclerView.adapter as LeaderboardAdapter).updateData(rankedLeaderboard) // ✅ Update data
        }

        // Load the leaderboard from ViewModel
        viewModel.loadLeaderboard()
    }


    private fun assignRanks(leaderboard: List<PlayerRank>): List<PlayerRank> {
        val sortedList = leaderboard.sortedByDescending { it.badges } // Sort by badges (highest first)
        val rankedList = mutableListOf<PlayerRank>()

        var currentRank = 1
        for ((index, player) in sortedList.withIndex()) {
            // Handle ties: If badge count is the same as the previous, keep the same rank
            if (index > 0 && player.badges == sortedList[index - 1].badges) {
                currentRank = rankedList.last().ranking
            } else {
                currentRank = index + 1
            }
            rankedList.add(PlayerRank(currentRank, player.username, player.badges))
        }

        return rankedList
    }
}
