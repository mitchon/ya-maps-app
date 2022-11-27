package com.komarov.ya_maps_app

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider

class TextSearchSession(
    private val context: MainActivity
): Session.SearchListener {

    override fun onSearchError(p0: Error) {
        Log.e("Search", "Search error")
    }

    override fun onSearchResponse(p0: Response) {
        val mapView = context.getMapView()
        for (item in p0.collection.children) {
            val point = item.obj?.geometry?.get(0)?.point
            if (point != null)
                mapView.map.mapObjects.addPlacemark(point, ImageProvider.fromResource(context, R.drawable.search_result))
                    .addTapListener(context)
        }
        p0.collection.children.let {
            val point = it.firstOrNull()?.obj?.geometry?.get(0)?.point
            val visible = mapView.map.visibleRegion
            if (point != null &&
                (point.latitude < visible.topLeft.latitude || point.latitude > visible.bottomLeft.latitude || point.longitude < visible.topLeft.longitude || point.longitude > visible.topRight.longitude)
            ) {
                mapView.map.move(
                    CameraPosition(point, 16f, 0f, 0f),
                    Animation(Animation.Type.SMOOTH, 1f),
                    null
                )
            }
        }
        RecyclerJob(context).showResults(p0.collection.children)
    }
}

