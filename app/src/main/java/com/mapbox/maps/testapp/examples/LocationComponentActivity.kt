package com.mapbox.maps.testapp.examples

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.fillExtrusionLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Anchor
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.light.generated.light
import com.mapbox.maps.extension.style.light.generated.setLight
import com.mapbox.maps.extension.style.projection.generated.getProjection
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.projection.generated.setProjection
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.LocationPuck3D
import com.mapbox.maps.plugin.PuckBearingSource
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.*
import com.mapbox.maps.testapp.R
import com.mapbox.maps.testapp.databinding.ActivityLocationComponentBinding
import com.mapbox.maps.testapp.utils.LocationPermissionHelper
import java.lang.ref.WeakReference

class LocationComponentActivity : AppCompatActivity() {

  private var lastStyleUri = Style.DARK
  private lateinit var locationPermissionHelper: LocationPermissionHelper
  private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
    // Jump to the current indicator position
    binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
    // Set the gestures plugin's focal point to the current indicator location.
    binding.mapView.gestures.focalPoint = binding.mapView.getMapboxMap().pixelForCoordinate(it)
  }
  private lateinit var binding: ActivityLocationComponentBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLocationComponentBinding.inflate(layoutInflater)
    setContentView(binding.root)
    locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
    locationPermissionHelper.checkPermissions {
      binding.mapView.apply {
        getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
          setupBuildings(it)
          it.setLight(
            light {
              anchor(Anchor.MAP)
              color(Color.YELLOW)
              position(
                radialCoordinate = 10.0,
                azimuthalAngle = 40.0,
                polarAngle = 50.0
              )
              castShadows(true)
              shadowIntensity(0.7)
            }
          )
          // Disable scroll gesture, since we are updating the camera position based on the indicator location.
          gestures.scrollEnabled = false
          gestures.addOnMapClickListener { point ->
            location
              .isLocatedAt(point) { isPuckLocatedAtPoint ->
                if (isPuckLocatedAtPoint) {
                  Toast.makeText(context, "Clicked on location puck", Toast.LENGTH_SHORT).show()
                }
              }
            true
          }
          gestures.addOnMapLongClickListener { point ->
            location.isLocatedAt(point) { isPuckLocatedAtPoint ->
              if (isPuckLocatedAtPoint) {
                Toast.makeText(context, "Long-clicked on location puck", Toast.LENGTH_SHORT)
                  .show()
              }
            }
            true
          }
          val locationProvider = location.getLocationProvider() as DefaultLocationProvider
          locationProvider.addOnCompassCalibrationListener {
            Toast.makeText(context, "Compass needs to be calibrated", Toast.LENGTH_LONG).show()
          }
        }
      }
    }
  }

  private fun setupBuildings(style: Style) {
    val fillExtrusionLayer = fillExtrusionLayer("3d-buildings", "composite") {
      sourceLayer("building")
      filter(Expression.eq(Expression.get("extrude"), Expression.literal("true")))
      minZoom(15.0)
      fillExtrusionColor(Color.parseColor("#aaaaaa"))
      fillExtrusionHeight(Expression.get("height"))
      fillExtrusionBase(Expression.get("min_height"))
      fillExtrusionOpacity(0.6)
      fillExtrusionAmbientOcclusionIntensity(0.3)
      fillExtrusionAmbientOcclusionRadius(3.0)
    }
    style.addLayer(fillExtrusionLayer)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_location_component, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_customise_location_puck_change -> {
        toggleCustomisedPuck()
        return true
      }
      R.id.action_map_style_change -> {
        toggleMapStyle()
        return true
      }
      R.id.action_map_projection_change -> {
        toggleMapProjection()
        return true
      }
      R.id.action_component_disable -> {
        binding.mapView.location.enabled = false
        return true
      }
      R.id.action_component_enabled -> {
        binding.mapView.location.enabled = true
        return true
      }
      R.id.action_show_bearing -> {
        if (binding.mapView.location.locationPuck is LocationPuck2D) {
          binding.mapView.location.apply {
            locationPuck = createDefault2DPuck(this@LocationComponentActivity, withBearing = true)
          }
        }
        return true
      }
      R.id.action_hide_bearing -> {
        if (binding.mapView.location.locationPuck is LocationPuck2D) {
          binding.mapView.location.apply {
            locationPuck = createDefault2DPuck(this@LocationComponentActivity)
          }
        }
        return true
      }
      R.id.heading -> {
        binding.mapView.location2.puckBearingSource = PuckBearingSource.HEADING
        item.isChecked = true
        return true
      }
      R.id.course -> {
        binding.mapView.location2.puckBearingSource = PuckBearingSource.COURSE
        item.isChecked = true
        return true
      }
      R.id.action_accuracy_enabled -> {
        binding.mapView.location2.showAccuracyRing = true
        item.isChecked = true
        return true
      }
      R.id.action_accuracy_disable -> {
        binding.mapView.location2.showAccuracyRing = false
        item.isChecked = true
        return true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  private fun toggleCustomisedPuck() {
    binding.mapView.location.let {
      when (it.locationPuck) {
        is LocationPuck3D -> it.locationPuck = LocationPuck2D(
          topImage = AppCompatResources.getDrawable(
            this,
            com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon
          ),
          bearingImage = AppCompatResources.getDrawable(
            this,
            com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_bearing_icon
          ),
          shadowImage = AppCompatResources.getDrawable(
            this,
            com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_stroke_icon
          ),
          scaleExpression = interpolate {
            linear()
            zoom()
            stop {
              literal(0.0)
              literal(0.6)
            }
            stop {
              literal(20.0)
              literal(1.0)
            }
          }.toJson()
        )
        is LocationPuck2D -> it.locationPuck = LocationPuck3D(
          modelUri = "asset://sportcar.glb",
          modelScale = listOf(0.1f, 0.1f, 0.1f),
          modelTranslation = listOf(0.1f, 0.1f, 0.1f),
          modelRotation = listOf(0.0f, 0.0f, 180.0f),
          modelCastShadows = false
        )
      }
    }
  }

  private fun toggleMapStyle() {
    val styleUrl = if (lastStyleUri == Style.DARK) Style.LIGHT else Style.DARK
    binding.mapView.getMapboxMap().loadStyleUri(styleUrl) {
      lastStyleUri = styleUrl
    }
  }

  private fun toggleMapProjection() {
    binding.mapView.getMapboxMap().getStyle { style ->
      style.setProjection(
        projection(
          when (style.getProjection().name) {
            ProjectionName.MERCATOR -> ProjectionName.GLOBE
            ProjectionName.GLOBE -> ProjectionName.MERCATOR
          }
        )
      )
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  override fun onStart() {
    super.onStart()
    binding.mapView.location
      .addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
  }

  override fun onStop() {
    super.onStop()
    binding.mapView.location
      .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
  }
}