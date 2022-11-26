package com.komarov.ya_maps_app

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        preferences = this.getSharedPreferences("MapPreferences", Context.MODE_PRIVATE)

        mapView = findViewById(R.id.mapview)
        val trafficLayer = MapKitFactory.getInstance().createTrafficLayer(mapView.mapWindow)
        trafficLayer.isTrafficVisible = false
        val toggleTrafficButton: Button = findViewById(R.id.toggleTrafficButton)
        toggleTrafficButton.setOnClickListener {
            when (trafficLayer.isTrafficVisible) {
                true -> {
                    trafficLayer.isTrafficVisible = false
                    toggleTrafficButton.background = ResourcesCompat.getDrawable(resources, R.drawable.on_screen_button, null)
                }
                false -> {
                    trafficLayer.isTrafficVisible = true
                    toggleTrafficButton.background = ResourcesCompat.getDrawable(resources, R.drawable.on_screen_button_active, null)
                }
            }
        }
        val locationButton: Button = findViewById(R.id.locationButton)
        locationButton.setOnClickListener {
            moveCamera()
        }
        requestPermission()
        moveCamera()
    }

    private fun onMapReady() {
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
    }

    private fun moveCamera() {
        if (this::userLocationLayer.isInitialized && userLocationLayer.cameraPosition() != null) {
            val location = userLocationLayer.cameraPosition()!!.target
            mapView.map.move(
                CameraPosition(location, 16f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
            saveDefaultLocation(location)
        } else {
            val point = getDefaultLocation()
            mapView.map.move(CameraPosition(point, 12f, 0f, 0f))
            saveDefaultLocation(point)
        }
    }

    private fun requestPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ), 1)
        } else
            onMapReady()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
                    userLocationLayer.isVisible = true
                    userLocationLayer.isHeadingEnabled = true
                }
                return
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun saveDefaultLocation(point: Point) {
        val editor = preferences.edit()
        editor.remove("defaultLatitude")
        editor.remove("defaultLongitude")
        editor.putString("defaultLatitude", point.latitude.toString())
        editor.putString("defaultLongitude", point.longitude.toString())
        editor.commit()
    }

    private fun getDefaultLocation(): Point {
        val latitude = preferences.getString("defaultLatitude", null)?.toDouble()
        val longitude = preferences.getString("defaultLongitude", null)?.toDouble()
        Log.w("Saved location", "$latitude $longitude")
        return Point(latitude ?: 55.752750, longitude ?: 37.619723)
    }
}