package com.komarov.ya_maps_app

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.Error
import kotlin.random.Random


class MainActivity : AppCompatActivity(), MapObjectTapListener, GeoObjectTapListener, DrivingSession.DrivingRouteListener {
    private lateinit var mapView: MapView
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var preferences: SharedPreferences
    private lateinit var searchManager: SearchManager
    private lateinit var drivingRouter: DrivingRouter
    private lateinit var drivingSession: DrivingSession
    var searchInProgress = false
    var destinationPoint: Point? = null

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
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        moveCamera()
        val searchButton: Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            mapView.map.mapObjects.clear()
            val query = findViewById<TextView>(R.id.searchInput).text.toString()
            searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.map.visibleRegion), SearchOptions(), TextSearchSession(this))
        }
        mapView.map.addTapListener(this)
        val routeButton: Button = findViewById(R.id.routeCreateButton)
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        routeButton.setOnClickListener{
            submitRequest()
        }
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

    fun getMapView(): MapView = mapView

    override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
        Log.w("Tap1", "${p1.latitude} ${p1.longitude}")
//        val coordinates = (p0 as PlacemarkMapObject).geometry
//        searchManager.submit(coordinates, null, SearchOptions(), TapSearchSession(this))
        mapView.map.move(
            CameraPosition(p1, 16f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
        return true
    }

    override fun onObjectTap(p0: GeoObjectTapEvent): Boolean {
        if (!searchInProgress) {
            val point = p0.geoObject.geometry[0].point
            Log.w("Tap2", "${point?.latitude} ${point?.longitude}")
            val coordinates = p0.geoObject.geometry[0].point ?: return false
            searchManager.submit(coordinates, null, SearchOptions(), TapSearchSession(this))
        }
        return true
    }

    override fun onBackPressed() {
        val recycler = findViewById<RecyclerView>(R.id.search_results_list)
        if (recycler.visibility == View.VISIBLE) {
            recycler.visibility = View.GONE
            return
        }
        if (searchInProgress) {
            searchInProgress = false
            findViewById<Button>(R.id.routeCreateButton).visibility = View.GONE
            mapView.map.mapObjects.clear()
            findViewById<EditText>(R.id.searchInput).text.clear()
            return
        }
        super.onBackPressed()
    }

    private fun submitRequest() {
        val pointStart = if (!this::userLocationLayer.isInitialized || userLocationLayer.cameraPosition() == null)
            getDefaultLocation().also { mapView.map.mapObjects.addPlacemark(it) }
        else
            userLocationLayer.cameraPosition()!!.target
        val requestPoints: ArrayList<RequestPoint> = arrayListOf(
            RequestPoint(pointStart, RequestPointType.WAYPOINT, null),
            RequestPoint(destinationPoint!!, RequestPointType.WAYPOINT, null)
        )
        drivingSession = drivingRouter.requestRoutes(requestPoints, DrivingOptions(), VehicleOptions(), this)
    }

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