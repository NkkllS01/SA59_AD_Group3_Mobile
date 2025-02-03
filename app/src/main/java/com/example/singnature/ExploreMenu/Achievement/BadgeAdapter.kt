package com.example.singnature.ExploreMenu.Achievement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.singnature.R

class BadgeAdapter(private val badgeList: List<Badge>) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val badgeImage: ImageView = itemView.findViewById(R.id.badgeImage)
        val badgeName: TextView = itemView.findViewById(R.id.badgeName)
        val badgeDescription: TextView = itemView.findViewById(R.id.badgeDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.badge_item, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badgeList[position]
        holder.badgeImage.setImageResource(badge.imageResId)
        holder.badgeName.text = badge.name
        holder.badgeDescription.text = badge.description
    }

    override fun getItemCount(): Int = badgeList.size
}