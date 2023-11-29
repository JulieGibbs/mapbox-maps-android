package com.mapbox.maps.plugin.locationcomponent

import android.util.Log
import androidx.annotation.RestrictTo
import com.mapbox.bindgen.Value
import com.mapbox.maps.MapboxLocationComponentException
import com.mapbox.maps.MapboxStyleManager
import com.mapbox.maps.logE

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class ModelSourceWrapper(
  val sourceId: String,
  private var url: String,
  position: List<Double>
) {

  private var sourceProperties = HashMap<String, Value>()
  private var style: MapboxStyleManager? = null

  init {
    val modelProperties = HashMap<String, Value>()
    modelProperties[URL] = Value(url)
    modelProperties[POSITION] = Value(position.map(::Value))
    modelProperties[ORIENTATION] = Value(listOf(0.0, 0.0, 0.0).map(::Value))

    val models = HashMap<String, Value>()
    models[DEFAULT_MODEL_NAME] = Value(modelProperties)

    sourceProperties["type"] = Value(TYPE)
    sourceProperties[MODELS] = Value(models)
  }

  fun updateStyle(style: MapboxStyleManager) {
    this.style = style
  }

  fun bindTo(style: MapboxStyleManager) {
    this.style = style
    val expected = style.addStyleSource(sourceId, toValue())
    expected.error?.let {
      Log.e(TAG, sourceProperties.toString())
      throw MapboxLocationComponentException("Add source failed: $it")
    }
  }

  fun setPositionAndOrientation(lngLat: List<Double>, orientation: List<Double>) {
    val property = hashMapOf(
      POSITION to Value(lngLat.map(::Value)),
      ORIENTATION to Value(orientation.map(::Value)),
      URL to Value(url)
    )
    val updateModel = hashMapOf(DEFAULT_MODEL_NAME to Value(property))
    updateProperty(MODELS, Value(updateModel))
  }

  fun toValue() = Value(sourceProperties)

  private fun updateProperty(propertyName: String, value: Value) {
    sourceProperties[propertyName] = value
    style?.let { style ->
      val expected = style.setStyleSourceProperty(
        sourceId,
        propertyName,
        value
      )
      expected.error?.let {
        logE(TAG, "Set source property \"${propertyName}\" failed:\nError: $it\nValue set: $value")
      }
    }
  }

  companion object {
    /** The name of default model*/
    const val DEFAULT_MODEL_NAME = "defaultModel"

    /** The key for models*/
    const val MODELS = "models"

    /** The key for source type*/
    const val TYPE = "model"

    /** The property key of url*/
    const val URL = "uri"

    /** The property key of position*/
    const val POSITION = "position"

    /** The property key of orientation*/
    const val ORIENTATION = "orientation"

    private const val TAG = "Mbgl-ModelSourceWrapper"
  }
}