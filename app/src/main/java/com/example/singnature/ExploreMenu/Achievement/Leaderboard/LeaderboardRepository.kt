package com.example.singnature.ExploreMenu.Achievement.Leaderboard

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelProvider
import com.example.singnature.databinding.ActivityLeaderboardBinding


class LeaderboardRepository(private val context: Context) {

    fun getRankedLeaderboard(): List<PlayerRank> {
        val query =
            "SELECT username, badges FROM leaderboard ORDER BY badges DESC"  // Fetch data sorted by badges

        // Get an instance of the readable database
        val database = DatabaseHelper(context).getReadableDatabaseInstance()

        // Execute the query using rawQuery
        val resultSet = database.rawQuery(query, null)

        val leaderboard = mutableListOf<PlayerRank>()
        var rank = 1
        var previousBadges = -1 // Keep track of the previous badge count to handle ties

        while (resultSet.moveToNext()) {
            val username = resultSet.getString(0)
            val badges = resultSet.getInt(1)

            // Check if badges are the same as the previous one; if so, keep the same rank
            if (badges != previousBadges) {
                previousBadges = badges
                rank = leaderboard.size + 1
            }

            leaderboard.add(PlayerRank(rank, username, badges))
        }
        resultSet.close()
        return leaderboard
    }
}

