package com.mapbox.maps

import android.app.Activity
import android.graphics.RectF
import android.os.Handler
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import com.mapbox.bindgen.Expected
import com.mapbox.bindgen.None
import com.mapbox.bindgen.Value
import com.mapbox.common.Cancelable
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.style.StyleContract
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin
import com.mapbox.maps.plugin.animation.CameraAnimationsPluginImpl
import com.mapbox.maps.plugin.delegates.*
import com.mapbox.maps.plugin.delegates.listeners.*
import com.mapbox.maps.plugin.gestures.GesturesPlugin
import com.mapbox.maps.plugin.gestures.GesturesPluginImpl
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

/**
 * The general class to interact with in the Mapbox Maps SDK for Android.
 * It exposes the entry point for all methods related to the Map object.
 * You cannot instantiate [MapboxMap] object directly, rather, you must obtain one
 * from the getMapboxMap() method MapView that you have
 * added to your application.
 *
 * Note: Similar to a View object, a MapboxMap should only be read and modified
 * from the main thread.
 *
 * @property style the map style.
 */
class MapboxMap :
  MapTransformDelegate,
  MapProjectionDelegate,
  MapFeatureQueryDelegate,
  ObservableInterface,
  MapListenerDelegate,
  MapPluginExtensionsDelegate,
  MapCameraManagerDelegate {

  private val nativeMap: NativeMapImpl
  private var isMapValid = true

  /**
   * Whether the [MapboxMap] instance is valid.
   *
   * [MapboxMap] becomes invalid after [MapView.onDestroy] is invoked,
   * calling any method then could result in undefined behaviour and will print an error log.
   *
   * Keeping the reference to an invalid [MapboxMap] instance introduces significant native memory leak.
   *
   * @return True if [MapboxMap] is valid and false otherwise.
   */
  fun isValid(): Boolean {
    return isMapValid
  }

  private val nativeObserver: NativeObserver
  private val observers = CopyOnWriteArraySet<Observer>()
  internal var style: Style? = null
  internal var isStyleLoadInitiated = false
  private val styleObserver: StyleObserver
  internal var renderHandler: Handler? = null

  /**
   * Represents current camera state.
   */
  override val cameraState: CameraState
    get() {
      checkNativeMap("cameraState")
      return nativeMap.getCameraState()
    }

  @VisibleForTesting(otherwise = PRIVATE)
  internal var cameraAnimationsPlugin: CameraAnimationsPlugin? = null

  @VisibleForTesting(otherwise = PRIVATE)
  internal var gesturesPlugin: GesturesPlugin? = null

  @VisibleForTesting(otherwise = PRIVATE)
  internal constructor(
    nativeMap: NativeMapImpl,
    nativeObserver: NativeObserver,
    styleObserver: StyleObserver
  ) {
    this.nativeMap = nativeMap
    this.nativeObserver = nativeObserver
    this.styleObserver = styleObserver
  }

  internal constructor(
    nativeMap: NativeMapImpl,
    nativeObserver: NativeObserver,
    pixelRatio: Float
  ) {
    this.nativeMap = nativeMap
    this.nativeObserver = nativeObserver
    this.styleObserver = StyleObserver(
      this.nativeMap.map,
      { style -> this.style = style },
      nativeObserver,
      pixelRatio
    )
  }

  /**
   * Will load a new map style asynchronous from the specified URI.
   *
   * URI can take the following forms:
   *
   * - **Constants**: load one of the bundled styles in [Style].
   *
   * - **`mapbox://styles/<user>/<style>`**:
   * loads the style from a [Mapbox account](https://www.mapbox.com/account/).
   * *user* is your username. *style* is the ID of your custom
   * style created in [Mapbox Studio](https://www.mapbox.com/studio).
   *
   * - **`http://...` or `https://...`**:
   * loads the style over the Internet from any web server.
   *
   * - **`asset://...`**:
   * loads the style from the APK *assets* directory.
   * This is used to load a style bundled with your app.
   *
   * - **`file://...`**:
   * loads the style from a file path. This is used to load a style from disk.
   *
   * Will load an empty json `{}` if the styleUri is empty.
   *
   * @param styleUri The style URI
   * @param styleTransitionOptions style transition options applied when loading the style
   * @param onStyleLoaded The OnStyleLoaded callback
   * @param onMapLoadErrorListener The OnMapLoadErrorListener callback
   */
  fun loadStyleUri(
    styleUri: String,
    styleTransitionOptions: TransitionOptions? = null,
    onStyleLoaded: Style.OnStyleLoaded? = null,
    onMapLoadErrorListener: OnMapLoadErrorListener? = null,
  ) {
    checkNativeMap("loadStyleUri")
    initializeStyleLoad(
      onStyleLoaded,
      styleDataStyleLoadedListener = {
        styleTransitionOptions?.let(it::setStyleTransition)
      },
      onMapLoadErrorListener = onMapLoadErrorListener,
    )
    if (styleUri.isEmpty()) {
      nativeMap.setStyleJSON(EMPTY_STYLE_JSON)
    } else {
      nativeMap.setStyleURI(styleUri)
    }
  }

  /**
   * Will load a new map style asynchronous from the specified URI.
   *
   * URI can take the following forms:
   *
   * - **Constants**: load one of the bundled styles in [Style].
   *
   * - **`mapbox://styles/<user>/<style>`**:
   * loads the style from a [Mapbox account](https://www.mapbox.com/account/).
   * *user* is your username. *style* is the ID of your custom
   * style created in [Mapbox Studio](https://www.mapbox.com/studio).
   *
   * - **`http://...` or `https://...`**:
   * loads the style over the Internet from any web server.
   *
   * - **`asset://...`**:
   * loads the style from the APK *assets* directory.
   * This is used to load a style bundled with your app.
   *
   * - **`file://...`**:
   * loads the style from a file path. This is used to load a style from disk.
   *
   * Will load an empty json `{}` if the styleUri is empty.
   *
   * @param styleUri The style URI
   * @param onStyleLoaded The OnStyleLoaded callback
   * @param onMapLoadErrorListener The OnMapLoadErrorListener callback
   */
  fun loadStyleUri(
    styleUri: String,
    onStyleLoaded: Style.OnStyleLoaded? = null,
    onMapLoadErrorListener: OnMapLoadErrorListener? = null
  ) = loadStyleUri(styleUri, null, onStyleLoaded, onMapLoadErrorListener)

  /**
   * Will load a new map style asynchronous from the specified URI.
   *
   * @param styleUri The style URI
   * @param onStyleLoaded The OnStyleLoaded callback
   */
  fun loadStyleUri(
    styleUri: String,
    onStyleLoaded: Style.OnStyleLoaded
  ) = loadStyleUri(styleUri, null, onStyleLoaded, null)

  /**
   * Will load a new map style asynchronous from the specified URI.
   *
   * @param styleUri The style URI
   */
  fun loadStyleUri(
    styleUri: String,
  ) = loadStyleUri(styleUri, null, null, null)

  /**
   * Load style JSON
   */
  fun loadStyleJson(
    styleJson: String,
    styleTransitionOptions: TransitionOptions? = null,
    onStyleLoaded: Style.OnStyleLoaded? = null,
    onMapLoadErrorListener: OnMapLoadErrorListener? = null,
  ) {
    checkNativeMap("loadStyleJson")
    initializeStyleLoad(
      onStyleLoaded,
      styleDataStyleLoadedListener = {
        styleTransitionOptions?.let(it::setStyleTransition)
      },
      onMapLoadErrorListener = onMapLoadErrorListener
    )
    nativeMap.setStyleJSON(styleJson)
  }

  /**
   * Load style JSON
   */
  fun loadStyleJson(
    styleJson: String,
    onStyleLoaded: Style.OnStyleLoaded? = null,
    onMapLoadErrorListener: OnMapLoadErrorListener? = null
  ) = loadStyleJson(styleJson, null, onStyleLoaded, onMapLoadErrorListener)

  /**
   * Load style JSON.
   */
  fun loadStyleJson(
    styleJson: String,
    onStyleLoaded: Style.OnStyleLoaded
  ) = loadStyleJson(styleJson, null, onStyleLoaded, null)

  /**
   * Load style JSON.
   */
  fun loadStyleJson(
    styleJson: String
  ) {
    checkNativeMap("loadStyleJson")
    loadStyleJson(styleJson, null, null, null)
  }

  /**
   * Load the style from Style Extension.
   */
  fun loadStyle(
    styleExtension: StyleContract.StyleExtension,
    transitionOptions: TransitionOptions? = null,
    onStyleLoaded: Style.OnStyleLoaded? = null,
    onMapLoadErrorListener: OnMapLoadErrorListener? = null,
  ) {
    checkNativeMap("loadStyle")
    initializeStyleLoad(
      onStyleLoaded = onStyleLoaded,
      styleDataStyleLoadedListener = { style ->
        styleExtension.light?.bindTo(style)
        styleExtension.terrain?.bindTo(style)
        styleExtension.atmosphere?.bindTo(style)
        styleExtension.projection?.bindTo(style)
        transitionOptions?.let(style::setStyleTransition)
      },
      styleDataSourcesLoadedListener = { style ->
        styleExtension.sources.forEach {
          it.bindTo(style)
        }
        styleExtension.layers.forEach { (layer, layerPosition) ->
          layer.bindTo(style, layerPosition)
        }
      },
      styleDataSpritesLoadedListener = { style ->
        styleExtension.images.forEach {
          it.bindTo(style)
        }
        // note - it is not strictly required to load models here, models can be loaded anytime during style load flow
        styleExtension.models.forEach {
          it.bindTo(style)
        }
      },
      onMapLoadErrorListener = onMapLoadErrorListener,
    )
    if (styleExtension.styleUri.isEmpty()) {
      nativeMap.setStyleJSON(EMPTY_STYLE_JSON)
    } else {
      nativeMap.setStyleURI(styleExtension.styleUri)
    }
  }

  /**
   * Load the style from Style Extension.
   */
  fun loadStyle(
    styleExtension: StyleContract.StyleExtension,
    onStyleLoaded: Style.OnStyleLoaded? = null,
    onMapLoadErrorListener: OnMapLoadErrorListener? = null,
  ) = loadStyle(styleExtension, null, onStyleLoaded, onMapLoadErrorListener)

  /**
   * Load the style from Style Extension.
   */
  fun loadStyle(
    styleExtension: StyleContract.StyleExtension,
    onStyleLoaded: Style.OnStyleLoaded
  ) = loadStyle(styleExtension, null, onStyleLoaded, null)

  /**
   * Load the style from Style Extension.
   */
  fun loadStyle(
    styleExtension: StyleContract.StyleExtension
  ) = loadStyle(styleExtension, null, null, null)

  private fun initializeStyleLoad(
    onStyleLoaded: Style.OnStyleLoaded? = null,
    styleDataStyleLoadedListener: Style.OnStyleLoaded,
    styleDataSpritesLoadedListener: Style.OnStyleLoaded? = null,
    styleDataSourcesLoadedListener: Style.OnStyleLoaded? = null,
    onMapLoadErrorListener: OnMapLoadErrorListener? = null,
  ) {
    style = null
    styleObserver.setLoadStyleListener(
      onStyleLoaded,
      styleDataStyleLoadedListener = styleDataStyleLoadedListener,
      styleDataSpritesLoadedListener = styleDataSpritesLoadedListener,
      styleDataSourcesLoadedListener = styleDataSourcesLoadedListener,
      onMapLoadErrorListener = onMapLoadErrorListener,
    )
    isStyleLoadInitiated = true
  }

  /**
   * Get the [Style] of the map asynchronously.
   *
   * Note: keeping the reference to an invalid [Style] instance introduces significant native memory leak,
   * see [Style.isValid] for more details.
   *
   * @param onStyleLoaded the callback to be invoked when the style is fully loaded
   */
  fun getStyle(onStyleLoaded: Style.OnStyleLoaded) {
    checkNativeMap("getStyle")
    style?.let(onStyleLoaded::onStyleLoaded)
      ?: styleObserver.addGetStyleListener(onStyleLoaded)
  }

  /**
   * Get the Style of the map synchronously, will return null is style is not loaded yet.
   *
   * Note: keeping the reference to an invalid [Style] instance introduces significant native memory leak,
   * see [Style.isValid] for more details.
   *
   * @return currently loaded [Style] object or NULL if it is not loaded.
   */
  fun getStyle(): Style? {
    checkNativeMap("getStyle")
    return style
  }

  /**
   * Get the ResourceOptions the map was initialized with.
   *
   * @return resourceOptions The resource options of the map
   */
  fun getResourceOptions(): ResourceOptions {
    checkNativeMap("getResourceOptions")
    return nativeMap.getResourceOptions()
  }

  /**
   * Clears temporary map data.
   *
   * Clears temporary map data from the data path defined in the given resource options.
   * Useful to reduce the disk usage or in case the disk cache contains invalid data.
   *
   * Note that calling this API will affect all maps that use the same data path and does not
   * affect persistent map data like offline style packages.
   *
   * @param callback Called once the request is complete or an error occurred.
   */
  fun clearData(callback: AsyncOperationResultCallback) {
    checkNativeMap("clearData")
    Map.clearData(nativeMap.getResourceOptions(), callback)
  }

  /**
   * Changes the map view by any combination of center, zoom, bearing, and pitch, without an animated transition.
   * The map will retain its current values for any details not passed via the camera options argument.
   * It is not guaranteed that the provided CameraOptions will be set, the map may apply constraints resulting in a
   * different CameraState.
   *
   * @param cameraOptions New camera options
   */
  override fun setCamera(cameraOptions: CameraOptions) {
    checkNativeMap("setCamera")
    nativeMap.setCamera(cameraOptions)
  }

  /**
   * Notify map about gesture being in progress.
   *
   * @param inProgress True if gesture is in progress
   */
  override fun setGestureInProgress(inProgress: Boolean) {
    checkNativeMap("setGestureInProgress")
    nativeMap.setGestureInProgress(inProgress)
  }

  /**
   * Returns if a gesture is in progress.
   *
   * @return Returns if a gesture is in progress
   */
  override fun isGestureInProgress(): Boolean {
    checkNativeMap("isGestureInProgress")
    return nativeMap.isGestureInProgress()
  }

  /**
   * Set the map north orientation
   *
   * @param northOrientation The map north orientation to set
   */
  override fun setNorthOrientation(northOrientation: NorthOrientation) {
    checkNativeMap("setNorthOrientation")
    nativeMap.setNorthOrientation(northOrientation)
  }

  /**
   * Set the map constrain mode
   *
   * @param constrainMode The map constraint mode to set
   */
  override fun setConstrainMode(constrainMode: ConstrainMode) {
    checkNativeMap("setConstrainMode")
    nativeMap.setConstrainMode(constrainMode)
  }

  /**
   * Set the map viewport mode
   *
   * @param viewportMode The map viewport mode to set
   */
  override fun setViewportMode(viewportMode: ViewportMode) {
    checkNativeMap("setViewportMode")
    nativeMap.setViewportMode(viewportMode)
  }

  /**
   * Set the map bounds.
   *
   * @param options the map bound options
   */
  override fun setBounds(options: CameraBoundsOptions): Expected<String, None> {
    checkNativeMap("setBounds")
    return nativeMap.setBounds(options)
  }

  /**
   * Get the map bounds options.
   *
   * @return Returns the map bounds options
   */
  override fun getBounds(): CameraBounds {
    checkNativeMap("getBounds")
    return nativeMap.getBounds()
  }

  /**
   * Tells the map rendering engine that the animation is currently performed by the
   * user (e.g. with a `setCamera()` calls series). It adjusts the engine for the animation use case.
   * In particular, it brings more stability to symbol placement and rendering.
   *
   * @param inProgress Bool representing if user animation is in progress
   */
  override fun setUserAnimationInProgress(inProgress: Boolean) {
    checkNativeMap("setUserAnimationInProgress")
    nativeMap.setUserAnimationInProgress(inProgress)
  }

  /**
   * Returns if user animation is currently in progress.
   *
   * @return Return true if a user animation is in progress.
   */
  override fun isUserAnimationInProgress(): Boolean {
    checkNativeMap("isUserAnimationInProgress")
    return nativeMap.isUserAnimationInProgress()
  }

  /**
   * Set the prefetch zoom delta
   *
   * @param delta The prefetch zoom delta
   */
  fun setPrefetchZoomDelta(delta: Byte) {
    checkNativeMap("setPrefetchZoomDelta")
    nativeMap.setPrefetchZoomDelta(delta)
  }

  /**
   * Get the prefetch zoom delta
   *
   * @return Returns the prefetch zoom delta
   */
  fun getPrefetchZoomDelta(): Byte {
    checkNativeMap("getPrefetchZoomDelta")
    return nativeMap.getPrefetchZoomDelta()
  }

  /**
   * Get map options.
   *
   * @return Returns map options
   */
  override fun getMapOptions(): MapOptions {
    checkNativeMap("getMapOptions")
    return nativeMap.getMapOptions()
  }

  /**
   * Gets the size of the map.
   *
   * @return size The size of the map in MapOptions#size platform pixels
   */
  override fun getSize(): Size {
    checkNativeMap("getSize")
    return nativeMap.getSize()
  }

  /**
   * Get debug options
   */
  fun getDebug(): List<MapDebugOptions> {
    checkNativeMap("getDebug")
    return nativeMap.getDebug()
  }

  /**
   * Set debug options
   */
  fun setDebug(debugOptions: List<MapDebugOptions>, enabled: Boolean) {
    checkNativeMap("setDebug")
    nativeMap.setDebug(debugOptions, enabled)
  }

  /**
   * Convert to a camera options from a given LatLngBounds, padding, bearing and pitch values.
   *
   * In order for this method to produce correct results [MapView] must be already
   * measured and inflated to have correct width and height values.
   * Calling this method in [Activity.onCreate] will lead to incorrect results.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching
   * the world bounds.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param bounds The LatLngBounds to take in account when converting
   * @param padding The optional padding to take in account when converting
   * @param bearing The optional bearing to take in account when converting
   * @param pitch The optional pitch to take in account when converting
   *
   * @return Returns the converted camera options
   */
  override fun cameraForCoordinateBounds(
    bounds: CoordinateBounds,
    padding: EdgeInsets?,
    bearing: Double?,
    pitch: Double?
  ): CameraOptions {
    checkNativeMap("cameraForCoordinateBounds")
    return nativeMap.cameraForCoordinateBounds(
      bounds,
      padding,
      bearing,
      pitch
    )
  }

  /**
   * Convert to a camera options from a given list of points, padding, bearing and pitch values.
   *
   * In order for this method to produce correct results [MapView] must be already
   * measured and inflated to have correct width and height values.
   * Calling this method in [Activity.onCreate] will lead to incorrect results.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching
   * the current camera center.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param coordinates The List of coordinates to take in account when converting
   * @param padding The optional padding to take in account when converting
   * @param bearing The optional bearing to take in account when converting
   * @param pitch The optional pitch to take in account when converting
   *
   * @return Returns the converted camera options
   */
  override fun cameraForCoordinates(
    coordinates: List<Point>,
    padding: EdgeInsets?,
    bearing: Double?,
    pitch: Double?
  ): CameraOptions {
    checkNativeMap("cameraForCoordinates")
    return nativeMap.cameraForCoordinates(coordinates, padding, bearing, pitch)
  }

  /**
   * Convenience method that returns the camera options object for given arguments
   *
   * In order for this method to produce correct results [MapView] must be already
   * measured and inflated to have correct width and height values.
   * Calling this method in [Activity.onCreate] will lead to incorrect results.
   *
   * Returns the camera options object for given arguments with zoom adjusted to fit \p coordinates into \p box, so that
   * coordinates on the left, top and right of \p camera.center fit into \p box.
   * Returns the provided camera options object unchanged upon error.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching
   * the current camera center.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param coordinates The coordinates representing the bounds of the map
   * @param box The box into which \p coordinates should fit
   * @param camera The camera for which zoom should be adjusted. Note that \p camera.center is required.
   *
   * @return Returns the camera options object with the zoom level adjusted to fit \p coordinates into \p box.
   */
  override fun cameraForCoordinates(
    coordinates: List<Point>,
    camera: CameraOptions,
    box: ScreenBox
  ): CameraOptions {
    checkNativeMap("cameraForCoordinates")
    return nativeMap.cameraForCoordinates(coordinates, camera, box)
  }

  /**
   * Convert to a camera options from a given geometry, padding, bearing and pitch values.
   *
   * In order for this method to produce correct results [MapView] must be already
   * measured and inflated to have correct width and height values.
   * Calling this method in [Activity.onCreate] will lead to incorrect results.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching
   * the current camera center.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param geometry The geometry to take in account when converting
   * @param padding The optional padding to take in account when converting
   * @param bearing The optional bearing to take in account when converting
   * @param pitch The optional pitch to take in account when converting
   *
   * @return Returns the converted camera options
   */
  override fun cameraForGeometry(
    geometry: Geometry,
    padding: EdgeInsets?,
    bearing: Double?,
    pitch: Double?
  ): CameraOptions {
    checkNativeMap("cameraForGeometry")
    return nativeMap.cameraForGeometry(geometry, padding, bearing, pitch)
  }

  /**
   * Returns the [CoordinateBounds] for a given camera.
   *
   * Note that if the given `camera` shows the antimeridian, the returned wrapped [CoordinateBounds]
   * might not represent the minimum bounding box.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching the world
   * bounds.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param camera The [CameraOptions] to use for calculating [CoordinateBounds].
   *
   * @return The [CoordinateBounds] object representing a given `camera`.
   */
  override fun coordinateBoundsForCamera(camera: CameraOptions): CoordinateBounds {
    checkNativeMap("coordinateBoundsForCamera")
    return nativeMap.coordinateBoundsForCamera(camera)
  }

  /**
   * Returns the [CoordinateBounds] for a given camera.
   *
   * This method is useful if the `camera` shows the antimeridian.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching the world
   * bounds.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param camera The [CameraOptions] to use for calculating [CoordinateBounds].
   *
   * @return The [CoordinateBounds] object representing a given `camera`.
   */
  override fun coordinateBoundsForCameraUnwrapped(camera: CameraOptions): CoordinateBounds {
    checkNativeMap("coordinateBoundsForCameraUnwrapped")
    return nativeMap.coordinateBoundsForCameraUnwrapped(camera)
  }

  /**
   *  Returns the coordinate bounds and zoom for a given camera.
   *
   * In order for this method to produce correct results [MapView] must be already
   * measured and inflated to have correct width and height values.
   * Calling this method in [Activity.onCreate] will lead to incorrect results.
   *
   * Note that if the given camera shows the antimeridian, the returned wrapped bounds
   * might not represent the minimum bounding box.
   *
   * See also {@link #coordinateBoundsZoomForCameraUnwrapped}
   *
   * This API isn't supported by Globe projection and will return a no-op result matching the world
   * bounds
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   *  @return Returns the coordinate bounds and zoom for a given camera.
   */
  override fun coordinateBoundsZoomForCamera(camera: CameraOptions): CoordinateBoundsZoom {
    checkNativeMap("coordinateBoundsZoomForCamera")
    return nativeMap.coordinateBoundsZoomForCamera(camera)
  }

  /**
   * Returns the unwrapped coordinate bounds and zoom for a given camera.
   *
   * In order for this method to produce correct results [MapView] must be already
   * measured and inflated to have correct width and height values.
   * Calling this method in [Activity.onCreate] will lead to incorrect results.
   *
   * This method is particularly useful, if the camera shows the antimeridian.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching the
   * world bounds.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   *  @return Returns the unwrapped coordinate bounds and zoom for a given camera.
   */
  override fun coordinateBoundsZoomForCameraUnwrapped(camera: CameraOptions): CoordinateBoundsZoom {
    checkNativeMap("coordinateBoundsZoomForCameraUnwrapped")
    return nativeMap.coordinateBoundsZoomForCameraUnwrapped(camera)
  }

  /**
   * Calculate a screen coordinate that corresponds to a geographical coordinate
   * (i.e., longitude-latitude pair).
   *
   * The screen coordinate is in [MapOptions.size] platform pixels relative to the top left
   * of the map (not of the whole screen).
   *
   * Map must be fully loaded for getting an altitude-compliant result if using 3D terrain.
   *
   * If the screen coordinate is outside of the bounds of [MapView] the returned screen coordinate
   * contains -1 for both coordinates.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching center of
   * the screen.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param coordinate A geographical coordinate on the map to convert to a screen coordinate.
   *
   * @return Returns a screen coordinate on the screen in [MapOptions.size] platform pixels. If the screen coordinate is outside of the bounds of [MapView] the returned screen coordinate contains -1 for both coordinates.
   */
  override fun pixelForCoordinate(coordinate: Point): ScreenCoordinate {
    checkNativeMap("pixelForCoordinate")
    val coordinate = nativeMap.pixelForCoordinate(coordinate)
    val screenSize = nativeMap.getSize()
    return if (coordinate.x in 0.0..screenSize.width.toDouble() && coordinate.y in 0.0..screenSize.height.toDouble()) {
      coordinate
    } else {
      ScreenCoordinate(-1.0, -1.0)
    }
  }

  /**
   * Calculate screen coordinates that corresponds to geographical coordinates
   * (i.e., longitude-latitude pair).
   *
   * The screen coordinates are in [MapOptions.size] platform pixels relative to the top left
   * of the map (not of the whole screen).
   *
   * Map must be fully loaded for getting an altitude-compliant result if using 3D terrain.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching the center
   * of the screen.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param coordinates A batch of geographical coordinates on the map to convert to screen coordinates.
   *
   * @return Returns a batch of screen coordinates on the screen in [MapOptions.size] platform pixels.
   */
  override fun pixelsForCoordinates(coordinates: List<Point>): List<ScreenCoordinate> {
    checkNativeMap("pixelsForCoordinates")
    return nativeMap.pixelsForCoordinates(coordinates.toMutableList())
  }

  /**
   * Calculate a geographical coordinate(i.e., longitude-latitude pair) that corresponds
   * to a screen coordinate.
   *
   * The screen coordinate is in [MapOptions.size] platform pixels relative to the top left
   * of the map (not of the whole screen).
   *
   * Map must be fully loaded for getting an altitude-compliant result if using 3D terrain.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching the center
   * of the screen.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param pixel A screen coordinate represented by x y coordinates.
   *
   * @return Returns a geographical coordinate corresponding to the x y coordinates
   * on the screen.
   */
  override fun coordinateForPixel(pixel: ScreenCoordinate): Point {
    checkNativeMap("coordinateForPixel")
    return nativeMap.coordinateForPixel(pixel)
  }

  /**
   * Calculate geographical coordinates(i.e., longitude-latitude pair) that corresponds
   * to screen coordinates.
   *
   * The screen coordinates are in [MapOptions.size] platform pixels relative to the top left
   * of the map (not of the whole screen).
   *
   * Map must be fully loaded for getting an altitude-compliant result if using 3D terrain.
   *
   * This API isn't supported by Globe projection and will return a no-op result matching the center
   * of the screen.
   * See [com.mapbox.maps.extension.style.projection.generated.setProjection]
   * and [com.mapbox.maps.extension.style.projection.generated.getProjection]
   *
   * @param pixels A batch of screen coordinates on the screen in [MapOptions.size] platform pixels.
   *
   * @return Returns a batch of geographical coordinates corresponding to the screen coordinates
   * on the screen.
   */
  override fun coordinatesForPixels(pixels: List<ScreenCoordinate>): List<Point> {
    checkNativeMap("coordinatesForPixels")
    return nativeMap.coordinatesForPixels(pixels.toMutableList())
  }

  /**
   * Calculates geographical `coordinates` (i.e., longitude-latitude pairs) that correspond
   * to a [RectF] that holds four screen points.
   *
   * The screen points are in `platform pixels` relative to the top left corner
   * of the map (not of the whole screen).
   *
   * This API isn't supported by Globe projection.
   *
   * @param rectF Rectangle with 4 edges (left, top, right, bottom).
   *
   * @return List of `geographical coordinates` that corresponds to given edges of RectF
   * in clockwise direction starting from topLeft. if provided [RectF] is empty, an
   * [IllegalArgumentException] will be thrown.
   */
  override fun coordinatesForRect(rectF: RectF): List<Point> {
    checkNativeMap("coordinatesForRect")
    if (rectF.isEmpty) throw IllegalArgumentException("RectF must not be empty")
    val screenCoordinateTopLeft = ScreenCoordinate(
      rectF.left.toDouble(), rectF.top.toDouble()
    )
    val screenCoordinateTopRight = ScreenCoordinate(
      rectF.right.toDouble(), rectF.top.toDouble()
    )
    val screenCoordinateBottomRight = ScreenCoordinate(
      rectF.right.toDouble(), rectF.bottom.toDouble()
    )
    val screenCoordinateBottomLeft = ScreenCoordinate(
      rectF.left.toDouble(), rectF.bottom.toDouble()
    )
    return nativeMap.coordinatesForPixels(
      mutableListOf(
        screenCoordinateTopLeft,
        screenCoordinateTopRight,
        screenCoordinateBottomRight,
        screenCoordinateBottomLeft
      )
    )
  }

  /**
   * Calculate distance spanned by one pixel at the specified latitude
   * and zoom level.
   *
   * @param latitude The latitude for which to return the value
   * @param zoom The zoom level
   *
   * @return Returns the distance measured in meters.
   */
  override fun getMetersPerPixelAtLatitude(latitude: Double, zoom: Double): Double {
    return Projection.getMetersPerPixelAtLatitude(latitude, zoom)
  }

  /**
   * Calculate distance spanned by one pixel at the specified latitude
   * at current zoom level.
   *
   * @param latitude The latitude for which to return the value
   *
   * @return Returns the distance measured in meters.
   */
  override fun getMetersPerPixelAtLatitude(latitude: Double): Double {
    return Projection.getMetersPerPixelAtLatitude(latitude, cameraState.zoom)
  }

  /**
   * Calculate Spherical Mercator ProjectedMeters coordinates.
   *
   * @param point A longitude-latitude pair for which to calculate
   * ProjectedMeters coordinates
   *
   * @return Returns Spherical Mercator ProjectedMeters coordinates
   */
  override fun projectedMetersForCoordinate(point: Point): ProjectedMeters {
    return Projection.projectedMetersForCoordinate(point)
  }

  /**
   * Calculate a longitude-latitude pair for a Spherical Mercator projected
   * meters.
   *
   * @param projectedMeters Spherical Mercator ProjectedMeters coordinates for
   * which to calculate a longitude-latitude pair.
   *
   * @return Returns a longitude-latitude pair.
   */
  override fun coordinateForProjectedMeters(projectedMeters: ProjectedMeters): Point {
    return Projection.coordinateForProjectedMeters(projectedMeters)
  }

  /**
   * Calculate a point on the map in Mercator Projection for a given
   * coordinate at the specified zoom scale.
   *
   * @param point The longitude-latitude pair for which to return the value.
   * @param zoomScale The current zoom factor applied on the map, is used to
   * calculate the world size as tileSize * zoomScale (i.e., 512 * 2 ^ Zoom level)
   * where tileSize is the width of a tile in pixels.
   *
   * @return Returns a point on the map in Mercator projection.
   */
  override fun project(point: Point, zoomScale: Double): MercatorCoordinate {
    return Projection.project(point, zoomScale)
  }

  /**
   * Calculate a coordinate for a given point on the map in Mercator Projection.
   *
   * @param coordinate Point on the map in Mercator projection.
   * @param zoomScale The current zoom factor applied on the map, is used to
   * calculate the world size as tileSize * zoomScale (i.e., 512 * 2 ^ Zoom level)
   * where tileSize is the width of a tile in pixels.
   *
   * @return Returns a coordinate.
   */
  override fun unproject(coordinate: MercatorCoordinate, zoomScale: Double): Point {
    return Projection.unproject(coordinate, zoomScale)
  }

  /**
   * Queries the map for rendered features.
   *
   * @param geometry The `screen pixel coordinates` (point, line string or box) to query for rendered features.
   * @param options The `render query options` for querying rendered features.
   * @param callback The `query features callback` called when the query completes.
   * @return A `cancelable` object that could be used to cancel the pending query.
   */
  override fun queryRenderedFeatures(
    geometry: RenderedQueryGeometry,
    options: RenderedQueryOptions,
    callback: QueryRenderedFeaturesCallback,
  ): Cancelable {
    checkNativeMap("queryRenderedFeatures", false)
    return nativeMap.queryRenderedFeatures(geometry, options, callback)
  }

  /**
   * Queries the map for source features.
   *
   * @param sourceId The style source identifier used to query for source features.
   * @param options The `source query options` for querying source features.
   * @param callback The `query features callback` called when the query completes.
   * @return A `cancelable` object that could be used to cancel the pending query.
   *
   * Note: In order to get expected results, the corresponding source needs to be in use and
   * the query shall be made after the corresponding source data is loaded.
   */
  override fun querySourceFeatures(
    sourceId: String,
    options: SourceQueryOptions,
    callback: QuerySourceFeaturesCallback
  ): Cancelable {
    checkNativeMap("querySourceFeatures", false)
    return nativeMap.querySourceFeatures(sourceId, options, callback)
  }

  /**
   * In some cases querying source / render features is expected to be a blocking operation
   * e.g. performing this action on map click. In this case in order to avoid deadlock on main
   * thread querying could be performed on render thread and in that case querying result will be also
   * delivered on render thread not leading to the main thread deadlock. Example:
   *
   * fun onMapClick() {
   *  executeOnRenderThread {
   *    queryRenderedFeatures(pixel, options) {
   *      // result callback called, do needed actions
   *      lock.notify()
   *    }
   *  }
   *  lock.wait()
   *  return false
   * }
   */
  override fun executeOnRenderThread(runnable: Runnable) {
    checkNativeMap("executeOnRenderThread")
    renderHandler?.post(runnable)
  }

  /**
   * Returns all the leaves (original points) of a cluster (given its cluster_id) from a GeoJsonSource, with pagination support: limit is the number of leaves
   * to return (set to Infinity for all points), and offset is the amount of points to skip (for pagination).
   *
   * Requires configuring the source as a cluster by calling [GeoJsonSource.Builder#cluster(boolean)].
   *
   * @param sourceIdentifier GeoJsonSource identifier.
   * @param cluster Cluster from which to retrieve leaves from
   * @param limit The number of points to return from the query (must use type [Long], set to maximum for all points). Defaults to 10.
   * @param offset The amount of points to skip (for pagination, must use type [Long]). Defaults to 0.
   * @param callback The result will be returned through the [QueryFeatureExtensionCallback].
   *         The result is a feature collection or a string describing an error if the operation was not successful.
   *
   * @return A `cancelable` object that could be used to cancel the pending query.
   */
  @JvmOverloads
  fun getGeoJsonClusterLeaves(
    sourceIdentifier: String,
    cluster: Feature,
    limit: Long = QFE_DEFAULT_LIMIT,
    offset: Long = QFE_DEFAULT_OFFSET,
    callback: QueryFeatureExtensionCallback,
  ): Cancelable = nativeMap.queryFeatureExtensions(
    /* sourceIdentifier = */ sourceIdentifier,
    /* feature = */ cluster,
    /* extension = */ QFE_SUPER_CLUSTER,
    /* extensionField = */ QFE_LEAVES,
    /* args = */ hashMapOf(QFE_LIMIT to Value(limit), QFE_OFFSET to Value(offset)),
    /* callback = */ callback
  )

  /**
   * Returns the children (original points or clusters) of a cluster (on the next zoom level)
   * given its id (cluster_id value from feature properties) from a GeoJsonSource.
   *
   * Requires configuring the source as a cluster by calling [GeoJsonSource.Builder#cluster(boolean)].
   *
   * @param sourceIdentifier GeoJsonSource identifier.
   * @param cluster cluster from which to retrieve children from
   * @param callback The result will be returned through the [QueryFeatureExtensionCallback].
   *         The result is a feature collection or a string describing an error if the operation was not successful.
   *
   * @return A `cancelable` object that could be used to cancel the pending query.
   */
  fun getGeoJsonClusterChildren(
    sourceIdentifier: String,
    cluster: Feature,
    callback: QueryFeatureExtensionCallback,
  ): Cancelable = nativeMap.queryFeatureExtensions(
    /* sourceIdentifier = */ sourceIdentifier,
    /* feature = */ cluster,
    /* extension = */ QFE_SUPER_CLUSTER,
    /* extensionField = */ QFE_CHILDREN,
    /* args = */ null,
    /* callback = */ callback
  )

  /**
   * Returns the zoom on which the cluster expands into several children (useful for "click to zoom" feature)
   * given the cluster's cluster_id (cluster_id value from feature properties) from a GeoJsonSource.
   *
   * Requires configuring the source as a cluster by calling [GeoJsonSource.Builder#cluster(boolean)].
   *
   * @param sourceIdentifier GeoJsonSource identifier.
   * @param cluster cluster from which to retrieve the expansion zoom from
   * @param callback The result will be returned through the [QueryFeatureExtensionCallback].
   *         The result is a feature extension value containing a value or a string describing an error if the operation was not successful.
   *
   * @return A `cancelable` object that could be used to cancel the pending query.
   */
  fun getGeoJsonClusterExpansionZoom(
    sourceIdentifier: String,
    cluster: Feature,
    callback: QueryFeatureExtensionCallback,
  ): Cancelable = nativeMap.queryFeatureExtensions(
    /* sourceIdentifier = */ sourceIdentifier,
    /* feature = */ cluster,
    /* extension = */ QFE_SUPER_CLUSTER,
    /* extensionField = */ QFE_EXPANSION_ZOOM,
    /* args = */ null,
    /* callback = */ callback
  )

  /**
   * Updates the state object of a feature within a style source.
   *
   * Update entries in the `state` object of a given feature within a style source. Only properties of the
   * `state` object will be updated. A property in the feature `state` object that is not listed in `state` will
   * retain its previous value. The properties must be paint properties, layout properties are not supported.
   *
   * Note that updates to feature `state` are asynchronous, so changes made by this method might not be
   * immediately visible using `getStateFeature`. And the corresponding source needs to be in use to ensure the
   * feature data it contains can be successfully updated.
   *
   * @param sourceId The style source identifier.
   * @param sourceLayerId The style source layer identifier (for multi-layer sources such as vector sources).
   * @param featureId The feature identifier of the feature whose state should be updated.
   * @param state The `state` object with properties to update with their respective new values.
   * @param callback The `feature state operation callback` called when the operation completes or ends.
   * @return A `cancelable` object that could be used to cancel the pending operation.
   *
   */
  @JvmOverloads
  fun setFeatureState(
    sourceId: String,
    sourceLayerId: String? = null,
    featureId: String,
    state: Value,
    callback: FeatureStateOperationCallback,
  ): Cancelable {
    checkNativeMap("setFeatureState")
    return nativeMap.setFeatureState(
      /* sourceId = */ sourceId,
      /* sourceLayerId = */ sourceLayerId,
      /* featureId = */ featureId,
      /* state = */ state,
      /* callback = */ callback
    )
  }

  /**
   * Get the state map of a feature within a style source.
   *
   * Note that updates to feature state are asynchronous, so changes made by other methods might not be
   * immediately visible.
   *
   * @param sourceId The style source identifier.
   * @param sourceLayerId The style source layer identifier (for multi-layer sources such as vector sources).
   * @param featureId The feature identifier of the feature whose state should be queried.
   * @param callback The `query feature state callback` called when the query completes.
   * @return A `cancelable` object that could be used to cancel the pending query.
   */
  @JvmOverloads
  fun getFeatureState(
    sourceId: String,
    sourceLayerId: String? = null,
    featureId: String,
    callback: QueryFeatureStateCallback,
  ): Cancelable {
    checkNativeMap("getFeatureState")
    return nativeMap.getFeatureState(
      /* sourceId = */ sourceId,
      /* sourceLayerId = */ sourceLayerId,
      /* featureId = */ featureId,
      /* callback = */callback
    )
  }

  /**
   * Removes entries from a feature state object.
   *
   * Remove a specified property or all property from a feature's state object, depending on the value of
   * `stateKey`.
   *
   * Note that updates to feature state are asynchronous, so changes made by this method might not be
   * immediately visible using `getStateFeature`.
   *
   * @param sourceId The style source identifier.
   * @param sourceLayerId The style source layer identifier (for multi-layer sources such as vector sources).
   * @param featureId The feature identifier of the feature whose state should be removed.
   * @param stateKey The key of the property to remove. If `null`, all feature's state object properties are removed.
   * @param callback The `feature state operation callback` called when the operation completes or ends.
   * @return A `cancelable` object that could be used to cancel the pending operation.
   */
  @JvmOverloads
  fun removeFeatureState(
    sourceId: String,
    sourceLayerId: String? = null,
    featureId: String,
    stateKey: String? = null,
    callback: FeatureStateOperationCallback,
  ): Cancelable {
    checkNativeMap("removeFeatureState")
    return nativeMap.removeFeatureState(
      /* sourceId = */ sourceId,
      /* sourceLayerId = */ sourceLayerId,
      /* featureId = */ featureId,
      /* stateKey = */ stateKey,
      /* callback = */ callback,
    )
  }

  /**
   * Reset all the feature states within a style source.
   *
   * Remove all feature state entries from the specified style source or source layer.
   *
   * Note that updates to feature state are asynchronous, so changes made by this method might not be
   * immediately visible using `getStateFeature`.
   *
   * @param sourceId The style source identifier.
   * @param sourceLayerId The style source layer identifier (for multi-layer sources such as vector sources).
   * @param callback The `feature state operation callback` called when the operation completes or ends.
   * @return A `cancelable` object that could be used to cancel the pending operation.
   */
  @JvmOverloads
  fun resetFeatureStates(sourceId: String, sourceLayerId: String? = null, callback: FeatureStateOperationCallback): Cancelable {
    checkNativeMap("resetFeatureState")
    return nativeMap.resetFeatureStates(sourceId, sourceLayerId, callback)
  }

  /**
   * Reduce memory use. Useful to call when the application gets paused or sent to background.
   */
  fun reduceMemoryUse() {
    checkNativeMap("reduceMemoryUse")
    nativeMap.reduceMemoryUse()
  }

  /**
   * Subscribes an Observer to a provided list of event types.
   * Observable will hold a strong reference to an \sa Observer instance, therefore,
   * in order to stop receiving notifications, caller must call unsubscribe with an
   * \sa Observer instance used for an initial subscription.
   *
   * @param observer an \sa Observer
   * @param events an array of event types to be subscribed to.
   */
  override fun subscribe(observer: Observer, events: List<String>) {
    checkNativeMap("subscribe")
    if (observers.add(observer)) {
      nativeMap.subscribe(observer, events.toMutableList())
    }
  }

  /**
   * Unsubscribes an Observer from a provided list of event types.
   *
   * @param observer an Observer
   * @param events an array of event types to be unsubscribed from.
   */
  override fun unsubscribe(observer: Observer, events: List<String>) {
    checkNativeMap("unsubscribe")
    if (observers.remove(observer)) {
      nativeMap.unsubscribe(observer, events.toMutableList())
    }
  }

  /**
   * Unsubscribes an Observer from all events.
   *
   * @param observer an Observer
   */
  override fun unsubscribe(observer: Observer) {
    checkNativeMap("unsubscribe")
    if (observers.remove(observer)) {
      nativeMap.unsubscribe(observer)
    }
  }

  /**
   * Add a listener that's going to be invoked whenever map camera changes.
   */
  override fun addOnCameraChangeListener(onCameraChangeListener: OnCameraChangeListener) {
    checkNativeMap("addOnCameraChangeListener")
    nativeObserver.addOnCameraChangeListener(onCameraChangeListener)
  }

  /**
   * Remove the camera change listener.
   */
  override fun removeOnCameraChangeListener(onCameraChangeListener: OnCameraChangeListener) {
    checkNativeMap("removeOnCameraChangeListener")
    nativeObserver.removeOnCameraChangeListener(onCameraChangeListener)
  }

  // Map events
  /**
   * Add a listener that's going to be invoked whenever map has entered the idle state.
   *
   * The Map is in the idle state when there are no ongoing transitions and the Map has rendered all
   * available tiles.
   */
  override fun addOnMapIdleListener(onMapIdleListener: OnMapIdleListener) {
    checkNativeMap("addOnMapIdleListener")
    nativeObserver.addOnMapIdleListener(onMapIdleListener)
  }

  /**
   * Remove the map idle listener.
   */
  override fun removeOnMapIdleListener(onMapIdleListener: OnMapIdleListener) {
    checkNativeMap("removeOnMapIdleListener")
    nativeObserver.removeOnMapIdleListener(onMapIdleListener)
  }

  /**
   * Add a listener that's going to be invoked whenever there's a map load error.
   */
  override fun addOnMapLoadErrorListener(onMapLoadErrorListener: OnMapLoadErrorListener) {
    checkNativeMap("addOnMapLoadErrorListener")
    nativeObserver.addOnMapLoadErrorListener(onMapLoadErrorListener)
  }

  /**
   * Remove the map error listener.
   */
  override fun removeOnMapLoadErrorListener(onMapLoadErrorListener: OnMapLoadErrorListener) {
    checkNativeMap("removeOnMapLoadErrorListener")
    nativeObserver.removeOnMapLoadErrorListener(onMapLoadErrorListener)
  }

  /**
   * Add a listener that's going to be invoked whenever the Map's style has been fully loaded, and
   * the Map has rendered all visible tiles.
   */
  override fun addOnMapLoadedListener(onMapLoadedListener: OnMapLoadedListener) {
    checkNativeMap("addOnMapLoadedListener")
    nativeObserver.addOnMapLoadedListener(onMapLoadedListener)
  }

  /**
   * Remove the map loaded listener.
   */
  override fun removeOnMapLoadedListener(onMapLoadedListener: OnMapLoadedListener) {
    checkNativeMap("removeOnMapLoadedListener")
    nativeObserver.removeOnMapLoadedListener(onMapLoadedListener)
  }

  // Render frame events
  /**
   * Add a listener that's going to be invoked whenever the Map started rendering a frame.
   */
  override fun addOnRenderFrameStartedListener(onRenderFrameStartedListener: OnRenderFrameStartedListener) {
    checkNativeMap("addOnRenderFrameStartedListener")
    nativeObserver.addOnRenderFrameStartedListener(onRenderFrameStartedListener)
  }

  /**
   * Remove the render frame started listener.
   */
  override fun removeOnRenderFrameStartedListener(onRenderFrameStartedListener: OnRenderFrameStartedListener) {
    checkNativeMap("removeOnRenderFrameStartedListener")
    nativeObserver.removeOnRenderFrameStartedListener(onRenderFrameStartedListener)
  }

  /**
   * Add a listener that's going to be invoked whenever the Map finished rendering a frame.
   *
   * The render-mode value tells whether the Map has all data ("full") required to render the visible viewport.
   * The needs-repaint value provides information about ongoing transitions that trigger Map repaint.
   * The placement-changed value tells if the symbol placement has been changed in the visible viewport.
   */
  override fun addOnRenderFrameFinishedListener(onRenderFrameFinishedListener: OnRenderFrameFinishedListener) {
    checkNativeMap("addOnRenderFrameFinishedListener")
    nativeObserver.addOnRenderFrameFinishedListener(onRenderFrameFinishedListener)
  }

  /**
   * Remove the render frame finished listener.
   */
  override fun removeOnRenderFrameFinishedListener(onRenderFrameFinishedListener: OnRenderFrameFinishedListener) {
    checkNativeMap("removeOnRenderFrameFinishedListener")
    nativeObserver.removeOnRenderFrameFinishedListener(onRenderFrameFinishedListener)
  }

  // Source events
  /**
   * Add a listener that's going to be invoked whenever a source has been added with StyleManager#addStyleSource
   * runtime API.
   */
  override fun addOnSourceAddedListener(onSourceAddedListener: OnSourceAddedListener) {
    checkNativeMap("addOnSourceAddedListener")
    nativeObserver.addOnSourceAddedListener(onSourceAddedListener)
  }

  /**
   * Remove the source added listener.
   */
  override fun removeOnSourceAddedListener(onSourceAddedListener: OnSourceAddedListener) {
    checkNativeMap("removeOnSourceAddedListener")
    nativeObserver.removeOnSourceAddedListener(onSourceAddedListener)
  }

  /**
   * Add a listener that's going to be invoked whenever the source data has been loaded.
   */
  override fun addOnSourceDataLoadedListener(onSourceDataLoadedListener: OnSourceDataLoadedListener) {
    checkNativeMap("addOnSourceDataLoadedListener")
    nativeObserver.addOnSourceDataLoadedListener(onSourceDataLoadedListener)
  }

  /**
   * Remove the source data loaded listener.
   */
  override fun removeOnSourceDataLoadedListener(onSourceDataLoadedListener: OnSourceDataLoadedListener) {
    checkNativeMap("removeOnSourceDataLoadedListener")
    nativeObserver.removeOnSourceDataLoadedListener(onSourceDataLoadedListener)
  }

  /**
   * Add a listener that's going to be invoked whenever a source has been removed with StyleManager#removeStyleSource
   * runtime API.
   */
  override fun addOnSourceRemovedListener(onSourceRemovedListener: OnSourceRemovedListener) {
    checkNativeMap("addOnSourceRemovedListener")
    nativeObserver.addOnSourceRemovedListener(onSourceRemovedListener)
  }

  /**
   * Remove the source removed listener.
   */
  override fun removeOnSourceRemovedListener(onSourceRemovedListener: OnSourceRemovedListener) {
    checkNativeMap("removeOnSourceRemovedListener")
    nativeObserver.removeOnSourceRemovedListener(onSourceRemovedListener)
  }

  // Style events
  /**
   * Add a listener that's going to be invoked whenever the requested style has been fully loaded,
   * including the style specified sprite and sources.
   */
  override fun addOnStyleLoadedListener(onStyleLoadedListener: OnStyleLoadedListener) {
    checkNativeMap("addOnStyleLoadedListener")
    nativeObserver.addOnStyleLoadedListener(onStyleLoadedListener)
  }

  /**
   * Remove the style loaded listener.
   */
  override fun removeOnStyleLoadedListener(onStyleLoadedListener: OnStyleLoadedListener) {
    checkNativeMap("removeOnStyleLoadedListener")
    nativeObserver.removeOnStyleLoadedListener(onStyleLoadedListener)
  }

  /**
   * Add a listener that's going to be invoked whenever the requested style data been loaded.
   * The 'type' property defines what kind of style data has been loaded.
   *
   * This event may be useful when application needs to modify style layers or sources and add or remove sources
   * before style is fully loaded.
   */
  override fun addOnStyleDataLoadedListener(onStyleDataLoadedListener: OnStyleDataLoadedListener) {
    checkNativeMap("addOnStyleDataLoadedListener")
    nativeObserver.addOnStyleDataLoadedListener(onStyleDataLoadedListener)
  }

  /**
   * Remove the style data loaded listener
   */
  override fun removeOnStyleDataLoadedListener(onStyleDataLoadedListener: OnStyleDataLoadedListener) {
    checkNativeMap("removeOnStyleDataLoadedListener")
    nativeObserver.removeOnStyleDataLoadedListener(onStyleDataLoadedListener)
  }

  /**
   * Add a listener that's going to be invoked whenever a style has a missing image.
   *
   * This event is emitted when the Map renders visible tiles and one of the required images is
   * missing in the sprite sheet.
   */
  override fun addOnStyleImageMissingListener(onStyleImageMissingListener: OnStyleImageMissingListener) {
    checkNativeMap("addOnStyleImageMissingListener")
    nativeObserver.addOnStyleImageMissingListener(onStyleImageMissingListener)
  }

  /**
   * Remove the style image missing listener.
   */
  override fun removeOnStyleImageMissingListener(onStyleImageMissingListener: OnStyleImageMissingListener) {
    checkNativeMap("removeOnStyleImageMissingListener")
    nativeObserver.removeOnStyleImageMissingListener(onStyleImageMissingListener)
  }

  /**
   * Add a listener that's going to be invoked whenever an image added to the Style is no longer
   * needed and can be removed using StyleManager#removeStyleImage method.
   */
  override fun addOnStyleImageUnusedListener(onStyleImageUnusedListener: OnStyleImageUnusedListener) {
    checkNativeMap("addOnStyleImageUnusedListener")
    nativeObserver.addOnStyleImageUnusedListener(onStyleImageUnusedListener)
  }

  /**
   * Remove the style image unused listener.
   */
  override fun removeOnStyleImageUnusedListener(onStyleImageUnusedListener: OnStyleImageUnusedListener) {
    checkNativeMap("removeOnStyleImageUnusedListener")
    nativeObserver.removeOnStyleImageUnusedListener(onStyleImageUnusedListener)
  }

  /**
   * Triggers a repaint of the map.
   */
  fun triggerRepaint() {
    checkNativeMap("triggerRepaint")
    nativeMap.triggerRepaint()
  }

  /**
   * Get the map's current free camera options. After mutation, it should be set back to the map.
   * @return The current free camera options.
   */
  override fun getFreeCameraOptions(): FreeCameraOptions {
    checkNativeMap("getFreeCameraOptions")
    return nativeMap.getFreeCameraOptions()
  }

  /**
   * Sets the map view with the free camera options.
   *
   * FreeCameraOptions provides more direct access to the underlying camera entity.
   * For backwards compatibility the state set using this API must be representable with
   * `CameraOptions` as well. Parameters are clamped to a valid range or discarded as invalid
   * if the conversion to the pitch and bearing presentation is ambiguous. For example orientation
   * can be invalid if it leads to the camera being upside down or the quaternion has zero length.
   *
   * @param freeCameraOptions The free camera options to set.
   */
  override fun setCamera(freeCameraOptions: FreeCameraOptions) {
    checkNativeMap("setCamera")
    nativeMap.setCamera(freeCameraOptions)
  }

  /**
   * Get elevation for given coordinate. Value is available only for the visible region on the screen, if terrain (DEM) tile is available.
   *
   * @param coordinate defined as longitude-latitude pair.
   *
   * @return Elevation (in meters) multiplied by current terrain exaggeration, or empty if elevation for the coordinate is not available.
   */
  fun getElevation(coordinate: Point): Double? {
    checkNativeMap("getElevation")
    return nativeMap.getElevation(coordinate)
  }

  /**
   * The memory budget hint to be used by the map. The budget can be given in
   * tile units or in megabytes. A Map will do the best effort to keep memory
   * allocations for a non essential resources within the budget.
   *
   * The memory budget distribution and resource
   * eviction logic is a subject to change. Current implementation sets memory budget
   * hint per data source.
   *
   * If null is set, the memory budget in tile units will be dynamically calculated based on
   * the current viewport size.
   *
   * @param memoryBudget The memory budget hint to be used by the Map.
   */
  @MapboxExperimental
  fun setMemoryBudget(memoryBudget: MapMemoryBudget?) {
    checkNativeMap("setMemoryBudget")
    nativeMap.setMemoryBudget(memoryBudget)
  }

  /**
   * Sets whether multiple copies of the world will be rendered side by side beyond -180 and 180 degrees longitude.
   * If disabled, when the map is zoomed out far enough that a single representation of the world
   * does not fill the map's entire container, there will be blank space beyond 180 and -180 degrees longitude.
   * In this case, features that cross 180 and -180 degrees longitude will be cut in two
   * (with one portion on the right edge of the map and the other on the left edge of the map) at every zoom level.
   *
   * By default, renderWorldCopies is set to `true`.
   *
   * @param renderWorldCopies The `boolean` value defining whether rendering world copies is going to be enabled or not.
   */
  fun setRenderWorldCopies(renderWorldCopies: Boolean) {
    checkNativeMap("setRenderWorldCopies")
    nativeMap.setRenderWorldCopies(renderWorldCopies)
  }

  /**
   * Returns whether multiple copies of the world are being rendered side by side beyond -180 and 180 degrees longitude.
   *
   * @return `true` if rendering world copies is enabled, `false` otherwise.
   */
  fun getRenderWorldCopies(): Boolean {
    checkNativeMap("getRenderWorldCopies")
    return nativeMap.getRenderWorldCopies()
  }

  /**
   * Prepares the drag gesture to use the provided screen coordinate as a pivot point.
   * This function should be called each time when user starts a dragging action (e.g. by clicking on the map).
   * The following dragging will be relative to the pivot.
   *
   * @param point The pivot coordinate, measured in \link MapOptions#size platform pixels \endlink from top to bottom and from left to right.
   */
  override fun dragStart(point: ScreenCoordinate) {
    checkNativeMap("dragStart")
    nativeMap.dragStart(point)
  }

  /**
   * Ends the ongoing drag gesture.
   * This function should be called always after the user has ended a drag gesture initiated by `dragStart`.
   */
  override fun dragEnd() {
    checkNativeMap("dragEnd")
    nativeMap.dragEnd()
  }

  /**
   * Calculates target point where camera should move after drag. The method should be called after `dragStart` and before `dragEnd`.
   *
   * @param fromPoint The point to drag the map from, measured in \link MapOptions#size platform pixels \endlink from top to bottom and from left to right.
   * @param toPoint The point to drag the map to, measured in \link MapOptions#size platform pixels \endlink from top to bottom and from left to right.
   *
   * @return Returns the camera options object showing end point
   */
  override fun getDragCameraOptions(
    fromPoint: ScreenCoordinate,
    toPoint: ScreenCoordinate
  ): CameraOptions {
    checkNativeMap("getDragCameraOptions")
    return nativeMap.getDragCameraOptions(fromPoint, toPoint)
  }

  internal fun setCameraAnimationPlugin(cameraAnimationsPlugin: CameraAnimationsPlugin) {
    if (cameraAnimationsPlugin is CameraAnimationsPluginImpl) {
      this.cameraAnimationsPlugin = cameraAnimationsPlugin
    } else {
      logW(TAG, "MapboxMap camera extension functions could work only with Mapbox developed plugin!")
    }
  }

  /**
   * Call extension function on [CameraAnimationsPlugin].
   * In most cases should not be called directly.
   */
  override fun cameraAnimationsPlugin(function: (CameraAnimationsPlugin.() -> Any?)): Any? {
    cameraAnimationsPlugin?.let {
      return function.invoke(it)
    }
    logW(
      TAG,
      "Either camera plugin is not added to the MapView or MapView has already been destroyed;" +
        " MapboxMap camera extension functions are no-op."
    )
    return null
  }

  internal fun setGesturesAnimationPlugin(gesturesPlugin: GesturesPlugin) {
    if (gesturesPlugin is GesturesPluginImpl) {
      this.gesturesPlugin = gesturesPlugin
    } else {
      logW(TAG, "MapboxMap gestures extension functions could work only with Mapbox developed plugin!")
    }
  }

  /**
   * Call extension function on [GesturesPlugin].
   * In most cases should not be called directly.
   */
  override fun gesturesPlugin(function: (GesturesPlugin.() -> Any?)): Any? {
    gesturesPlugin?.let {
      return function.invoke(it)
    }
    logW(
      TAG,
      "Either gestures plugin is not added to the MapView or MapView has already been destroyed;" +
        " MapboxMap gestures extension functions are no-op."
    )
    return null
  }

  internal fun onDestroy() {
    cameraAnimationsPlugin = null
    gesturesPlugin = null
    observers.forEach {
      nativeMap.unsubscribe(it)
    }
    observers.clear()
    styleObserver.onDestroy()
    isMapValid = false
  }

  internal fun addViewAnnotation(
    viewId: String,
    options: ViewAnnotationOptions
  ): Expected<String, None> {
    checkNativeMap("addViewAnnotation")
    return nativeMap.addViewAnnotation(viewId, options)
  }

  internal fun updateViewAnnotation(
    viewId: String,
    options: ViewAnnotationOptions
  ): Expected<String, None> {
    checkNativeMap("updateViewAnnotation")
    return nativeMap.updateViewAnnotation(viewId, options)
  }

  internal fun removeViewAnnotation(viewId: String): Expected<String, None> {
    checkNativeMap("removeViewAnnotation")
    return nativeMap.removeViewAnnotation(viewId)
  }

  internal fun getViewAnnotationOptions(identifier: String): Expected<String, ViewAnnotationOptions> {
    checkNativeMap("getViewAnnotationOptions")
    return nativeMap.getViewAnnotationOptions(identifier)
  }

  internal fun setViewAnnotationPositionsUpdateListener(listener: DelegatingViewAnnotationPositionsUpdateListener?) {
    checkNativeMap("setViewAnnotationPositionsUpdateListener")
    return nativeMap.setViewAnnotationPositionsUpdateListener(listener)
  }

  private fun checkNativeMap(methodName: String, checkMainThread: Boolean = true) {
    if (checkMainThread) {
      ThreadChecker.throwIfNotMainThread()
    }
    if (!isMapValid) {
      logW(
        TAG,
        "MapboxMap object (accessing $methodName) should not be stored and used after MapView is destroyed."
      )
    }
  }

  /**
   * A convenience object to access MapboxMap's static utilities.
   */
  companion object {
    /**
     * Clears temporary map data.
     *
     * Clears temporary map data from the data path defined in the given resource options.
     * Useful to reduce the disk usage or in case the disk cache contains invalid data.
     *
     * Note that calling this API will affect all maps that use the same data path and does not
     * affect persistent map data like offline style packages.
     *
     * @param resourceOptions The `resource options` that contain the map data path to be used
     * @param callback Called once the request is complete or an error occurred.
     */
    @JvmStatic
    fun clearData(resourceOptions: ResourceOptions, callback: AsyncOperationResultCallback) {
      Map.clearData(resourceOptions, callback)
    }

    private const val TAG = "Mbgl-MapboxMap"
    private const val EMPTY_STYLE_JSON = "{}"
    internal const val QFE_SUPER_CLUSTER = "supercluster"
    internal const val QFE_LEAVES = "leaves"
    internal const val QFE_LIMIT = "limit"
    internal const val QFE_OFFSET = "offset"
    internal const val QFE_DEFAULT_LIMIT = 10L
    internal const val QFE_DEFAULT_OFFSET = 0L
    internal const val QFE_CHILDREN = "children"
    internal const val QFE_EXPANSION_ZOOM = "expansion-zoom"
  }
}