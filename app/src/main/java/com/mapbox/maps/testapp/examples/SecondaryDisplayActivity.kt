package com.mapbox.maps.testapp.examples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.testapp.R
import com.mapbox.maps.testapp.databinding.ActivitySecondaryDisplayBinding

/**
 * Example of displaying a map on a secondary display.
 */
class SecondaryDisplayActivity : AppCompatActivity() {

  private lateinit var binding: ActivitySecondaryDisplayBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySecondaryDisplayBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.mapView.mapboxMap.loadStyle(
      style(Style.STANDARD) {
        +image(
          IMAGE_ID,
          ContextCompat.getDrawable(
            this@SecondaryDisplayActivity,
            R.drawable.ic_red_marker
          )!!.toBitmap()
        ) {
          // Note: The default scale doesn't work with secondary displays.
          // we need to manually set the scale to the pixel for the current context
          scale(this@SecondaryDisplayActivity.resources.displayMetrics.density)
        }
        +geoJsonSource(SOURCE_ID) {
          geometry(HELSINKI)
        }
        +symbolLayer(LAYER_ID, SOURCE_ID) {
          iconImage(IMAGE_ID)
          iconAllowOverlap(true)
        }
      }
    )
    binding.moveCameraButton.setOnClickListener {
      moveCamera()
    }
  }

  private fun moveCamera() {
    val cameraOption = CameraOptions.Builder()
      .center(HELSINKI)
      .zoom(ZOOM)
      .build()

    binding.mapView.mapboxMap.flyTo(
      cameraOption,
      MapAnimationOptions.mapAnimationOptions {
        duration(DURATION)
      }
    )
  }

  companion object {
    private val HELSINKI = Point.fromLngLat(24.9384, 60.1699)
    private const val ZOOM = 14.0
    private const val DURATION = 3000L
    private const val IMAGE_ID = "test-image"
    private const val LAYER_ID = "symbol-layer-id"
    private const val SOURCE_ID = "source-id"
  }
}