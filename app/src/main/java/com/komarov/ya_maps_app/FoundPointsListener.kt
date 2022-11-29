package com.komarov.ya_maps_app

import android.util.Log
import android.view.View
import android.widget.Button
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView

class FoundPointsListener(
    private val context: MainActivity
): MapObjectTapListener {
    override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
        Log.w("Tap1", "${p1.latitude} ${p1.longitude}")
        val mapView = context.getMapView()
        mapView.map.move(
            CameraPosition(p1, 16f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
        context.searchInProgress = true
        context.findViewById<Button>(R.id.routeCreateButton).visibility = View.VISIBLE
        context.destinationPoint = p1
        return true
    }
}