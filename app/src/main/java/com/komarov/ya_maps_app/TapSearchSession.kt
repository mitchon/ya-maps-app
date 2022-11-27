package com.komarov.ya_maps_app

import android.util.Log
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error

class TapSearchSession(private val context: MainActivity): Session.SearchListener {
    override fun onSearchError(p0: Error) {
        Log.e("Search", "Search error")
    }

    override fun onSearchResponse(p0: Response) {
        RecyclerJob(context).showResults(p0.collection.children)
    }
}