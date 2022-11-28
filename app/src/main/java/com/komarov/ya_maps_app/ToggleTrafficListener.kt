package com.komarov.ya_maps_app

import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import com.yandex.mapkit.traffic.TrafficLayer

class ToggleTrafficListener(
    private val trafficLayer: TrafficLayer
): OnClickListener {
    override fun onClick(v: View) {
        val toggleTrafficButton: Button = v.findViewById(R.id.toggleTrafficButton)
        when (trafficLayer.isTrafficVisible) {
            true -> {
                trafficLayer.isTrafficVisible = false
                toggleTrafficButton.background = ResourcesCompat.getDrawable(v.resources, R.drawable.on_screen_button, null)
            }
            false -> {
                trafficLayer.isTrafficVisible = true
                toggleTrafficButton.background = ResourcesCompat.getDrawable(v.resources, R.drawable.on_screen_button_active, null)
            }
        }
    }
}