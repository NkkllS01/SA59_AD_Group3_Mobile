package com.example.singnature.WildlifeMenu

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.singnature.R

class SightingsSearchResultsAdapter (
    private val context: Context,
    private val data: List<Sightings>
) : BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Sightings = data[position]
    override fun getItemId(position: Int): Long = data[position].sightingId.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.itemImage)
        val textView = view.findViewById<TextView>(R.id.itemText)
        val reportByText = view.findViewById<TextView>(R.id.reportByText)

        val item = getItem(position)

        textView.text = item.specieName
        reportByText.text = "Reported by: ${item.userName}"

        Log.d("SightingsAdapter", "Image URL: ${item.imageUrl}")

        if (!item.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(imageView)
        } else {
            Log.d("SightingsAdapter", "Using placeholder for Sighting ID: ${item.sightingId}")
            imageView.setImageResource(R.drawable.image_placeholder)
        }

        return view
    }
}