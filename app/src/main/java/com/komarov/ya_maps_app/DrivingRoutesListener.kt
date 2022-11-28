package com.komarov.ya_maps_app

import android.graphics.Color
import android.util.Log
import com.yandex.mapkit.Animation
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import kotlin.random.Random

class DrivingRoutesListener(
    private val mapView: MapView
): DrivingSession.DrivingRouteListener {
    override fun onDrivingRoutes(p0: List<DrivingRoute>) {
        for (route in p0) {
            val polyline = mapView.map.mapObjects.addPolyline(route.geometry)
            polyline.outlineColor = Color.BLACK
            polyline.setStrokeColor(Color.argb(255, Random.nextInt(), Random.nextInt(), Random.nextInt()))

            val cameraPosition = mapView.map.cameraPosition(polyline.geometry.let { BoundingBox(it.points.first(), it.points.last()) })
            mapView.map.move(
                CameraPosition(cameraPosition.target, cameraPosition.zoom - 1.0f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    override fun onDrivingRoutesError(p0: Error) {
        Log.e("DrivingRoutes", p0.toString())
    }
}