package com.example.singnature.WildlifeMenu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.view.View
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.singnature.R

data class SpeciesCategory(val id: Int, val name: String, val imageResId: Int)

class SpeciesCategoryAdapter(private val context: Context, private val speciesCategories: List<SpeciesCategory>) : BaseAdapter() {
    override fun getCount(): Int = speciesCategories.size
    override fun getItem(position: Int): SpeciesCategory = speciesCategories[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_species_category, parent, false)
        val speciesCategory = getItem(position)

        val imageView = view.findViewById<ImageView>(R.id.categoryImage)
        val textView = view.findViewById<TextView>(R.id.categoryName)

        imageView.setImageResource(speciesCategory.imageResId)
        textView.text = speciesCategory.name

        return view
    }
}
