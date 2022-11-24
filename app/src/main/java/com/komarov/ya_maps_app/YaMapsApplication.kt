package com.komarov.ya_maps_app

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class YaMapsApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("c7a87392-cadb-40db-9eef-ce94298aff95")
    }
}