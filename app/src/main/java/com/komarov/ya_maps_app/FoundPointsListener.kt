package com.komarov.ya_maps_app

import android.util.Log
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView

class FoundPointsListener(
    private val mapView: MapView
): MapObjectTapListener {
    override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
        Log.w("Tap1", "${p1.latitude} ${p1.longitude}")
        mapView.map.move(
            CameraPosition(p1, 16f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
        return true
    }
}