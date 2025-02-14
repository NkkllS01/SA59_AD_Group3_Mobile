import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.singnature.R
import com.example.singnature.WildlifeMenu.SpeciesCategory

class SpeciesCategoryAdapter(private val context: Context, private val categories: List<SpeciesCategory>) : BaseAdapter() {
    override fun getCount(): Int = categories.size
    override fun getItem(position: Int): Any = categories[position]
    override fun getItemId(position: Int): Long = categories[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_species_category, parent, false)
        val category = categories[position]

        val imageView = view.findViewById<ImageView>(R.id.categoryImage)
        val textView = view.findViewById<TextView>(R.id.categoryName)

        textView.text = category.name
        Glide.with(context).load(category.imageUrl).placeholder(R.drawable.image_placeholder).into(imageView)

        return view
    }
}
