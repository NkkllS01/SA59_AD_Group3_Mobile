package com.example.singnature.ExploreMenu.Achievement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.singnature.ExploreMenu.Achievement.Leaderboard.LeaderboardActivity
import com.example.singnature.R

class AchievementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)  // This should match your layout file name

        val achievementButton = findViewById<Button>(R.id.badgeButton)
        val leaderboardButton = findViewById<Button>(R.id.leaderboardButton)

        leaderboardButton.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }
        achievementButton.setOnClickListener {
            val intent = Intent(this, BadgeActivity::class.java)
            startActivity(intent)
        }
    }
}
