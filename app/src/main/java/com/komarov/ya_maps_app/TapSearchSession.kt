package com.komarov.ya_maps_app

import android.app.AlertDialog
import android.util.Log
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error

class TapSearchSession(private val context: MainActivity): Session.SearchListener {
    override fun onSearchError(p0: Error) {
        Log.e("Search", "Search error")
    }

    override fun onSearchResponse(p0: Response) {
        for (i in p0.collection.children) {
            val item = i.obj
            if (item != null) {
                val business =
                    item.metadataContainer.getItem(BusinessObjectMetadata::class.java)
                val toponym =
                    item.metadataContainer.getItem(ToponymObjectMetadata::class.java)
                val description = "${item.name ?: ""}\n" +
                        "Description:\n${item.descriptionText ?: ""}\n" +
                        "Business:\n${business?.name ?: ""}\n" +
                        "Toponym:\n${toponym?.address ?: ""}\n"
                AlertDialog.Builder(context)
                    .setTitle("Result")
                    .setMessage(description)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }
}