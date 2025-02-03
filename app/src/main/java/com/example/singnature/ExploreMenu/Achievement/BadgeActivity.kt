package com.example.singnature.ExploreMenu.Achievement

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.singnature.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BadgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)

        val username = intent.getStringExtra("username")
        val num_badges = intent.getIntExtra("num_badges",0)

        val textView: TextView = findViewById(R.id.achievementsTextView)
        textView.text = "Hi, $username! You have collected $num_badges badges!"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://localhost:5221/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val badgeApi = retrofit.create(BadgeApi::class.java)


        val badgeRecyclerView: RecyclerView = findViewById(R.id.badgeRecyclerView)

        // Example badge data
        val badges = listOf(
            Badge("Explorer", "Awarded for catching all the species at 10 locations", R.drawable.ic_explorer_badge),
            Badge("Collector", "Awarded for collecting 50 items", R.drawable.ic_collector_badge),
            Badge("First Catch", "Congratulation for catching your first species", R.drawable.ic_firstcatch_badge)
        )

        badgeRecyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns
        badgeRecyclerView.adapter = BadgeAdapter(badges)
    }
}