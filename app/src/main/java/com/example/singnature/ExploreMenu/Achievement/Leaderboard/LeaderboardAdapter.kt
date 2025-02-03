package com.example.singnature.ExploreMenu.Achievement.Leaderboard

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.singnature.R


class LeaderboardAdapter(private var leaderboard: List<PlayerRank>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    fun updateData(newLeaderboard: List<PlayerRank>) {
        leaderboard = newLeaderboard
        notifyDataSetChanged() // Notify RecyclerView about data changes
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.leaderboard_item, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        if (position == 0) {
            // Header row
            holder.rankTextView.text = "Rank"
            holder.usernameTextView.text = "Username"
            holder.badgesTextView.text = "Badges"
            holder.rankTextView.setTypeface(null, Typeface.BOLD)
            holder.usernameTextView.setTypeface(null, Typeface.BOLD)
            holder.badgesTextView.setTypeface(null, Typeface.BOLD)
        } else {
            // Data row
            val player = leaderboard[position - 1] // Adjust for header row
            holder.rankTextView.text = player.ranking.toString()
            holder.usernameTextView.text = player.username
            holder.badgesTextView.text = player.badges.toString()
        }
    }

    override fun getItemCount(): Int {
        return leaderboard.size + 1 // Add 1 for header
    }

    class LeaderboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankTextView: TextView = view.findViewById(R.id.rankTextView)
        val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
        val badgesTextView: TextView = view.findViewById(R.id.badgesTextView)
    }
}
