package com.komarov.ya_maps_app

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.image.ImageProvider

class RecyclerJob(val context: MainActivity) {
    private fun onClick(results: List<GeoObjectCollection.Item>, position: Int) {
        val mapView = context.getMapView()
        val point = results[position].obj?.geometry?.get(0)?.point
        if (point != null) {
            mapView.map.mapObjects.addPlacemark(point, ImageProvider.fromResource(context, R.drawable.search_result))
            context.findViewById<Button>(R.id.pathCreateButton).visibility = View.VISIBLE
            mapView.map.move(
                CameraPosition(point, 16f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    fun showResults(results: List<GeoObjectCollection.Item>) {
        context.searchInProgress = true
        val recyclerView: RecyclerView = context.findViewById(R.id.search_results_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = SearchResultsRecyclerAdapter(ArrayList(results.mapNotNull { it.obj?.name })) {
            onClick(results, it)
            recyclerView.visibility = View.GONE
        }
        recyclerView.adapter = adapter
        recyclerView.visibility = View.VISIBLE
    }
}