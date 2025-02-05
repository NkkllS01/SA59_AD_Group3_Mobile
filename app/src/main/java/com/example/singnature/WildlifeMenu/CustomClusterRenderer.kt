package com.example.singnature.WildlifeMenu

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.example.singnature.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class CustomClusterRenderer(
    private val context: Context, map: GoogleMap, clusterManager: ClusterManager<Sightings>
) : DefaultClusterRenderer<Sightings>(context, map, clusterManager) {

    override fun onBeforeClusterRendered(cluster: Cluster<Sightings>, markerOptions: com.google.android.gms.maps.model.MarkerOptions) {
        val icon = getClusterIcon(cluster)
        markerOptions.icon(icon)
    }

    override fun onClusterUpdated(cluster: Cluster<Sightings>, marker: com.google.android.gms.maps.model.Marker) {
        marker.setIcon(getClusterIcon(cluster))
    }

    override fun onClustersChanged(clusters: MutableSet<out Cluster<Sightings>>?) {
        super.onClustersChanged(clusters)
        clusters?.forEach { cluster ->
            val marker = getMarker(cluster)
            marker?.setIcon(getClusterIcon(cluster))
        }
    }

    private fun getClusterIcon(cluster: Cluster<Sightings>): BitmapDescriptor {
        val clusterSize = cluster.size
        val radius = when {
            clusterSize < 5 -> 70f
            clusterSize < 15 -> 100f
            else -> 130f
        }

        val bitmap = Bitmap.createBitmap((radius * 2).toInt(), (radius * 2).toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint().apply {
            color = getClusterColor(clusterSize)
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        canvas.drawCircle(radius, radius, radius, paint)

        val borderPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawCircle(radius, radius, radius - borderPaint.strokeWidth / 2, borderPaint)

        paint.apply {
            color = Color.BLACK
            textSize = 60f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText(clusterSize.toString(), radius, radius + 20, paint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getClusterColor(size: Int): Int {
        return when {
            size < 5 -> ContextCompat.getColor(context, R.color.orange_light)
            size < 15 -> ContextCompat.getColor(context, R.color.orange_mid)
            else -> ContextCompat.getColor(context, R.color.orange_dark)
        }
    }
}