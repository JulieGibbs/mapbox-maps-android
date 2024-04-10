// This file is generated.

package com.mapbox.maps.extension.compose.style.sources.generated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.mapbox.bindgen.Value
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.style.IdGenerator.generateRandomSourceId
import com.mapbox.maps.extension.compose.style.sources.SourceState

/**
 * Create and [rememberSaveable] a [GeoJsonSourceState] using [GeoJsonSourceState.Saver].
 * [init] will be called when the [GeoJsonSourceState] is first created to configure its
 * initial state.
 *
 * @param key An optional key to be used as a key for the saved value. If not provided we use the
 * automatically generated by the Compose runtime which is unique for the every exact code location
 * in the composition tree.
 * @param sourceId The optional sourceId for the source state, by default, a random source ID will be used.
 * @param init A function initialise this [GeoJsonSourceState].
 */
@Composable
@MapboxExperimental
public inline fun rememberGeoJsonSourceState(
  key: String? = null,
  sourceId: String = remember {
    generateRandomSourceId("geojson")
  },
  crossinline init: GeoJsonSourceState.() -> Unit = {}
): GeoJsonSourceState = rememberSaveable(key = key, saver = GeoJsonSourceState.Saver) {
  GeoJsonSourceState(sourceId).apply(init)
}

/**
 * A GeoJSON data source.
 *
 * @see [The online documentation](https://docs.mapbox.com/style-spec/reference/sources#geojson)
 *
 * @param sourceId The id of the source state, by default a random generated ID will be used.
 * @param initialBuilderProperties The initial immutable properties of the source.
 * @param initialProperties The initial mutable properties of the source.
 * @param initialData The initial [GeoJSONData] of the source.
 */
@MapboxExperimental
public class GeoJsonSourceState(
  override val sourceId: String = generateRandomSourceId("geojson"),
  initialData: GeoJSONData = GeoJSONData.default,
  initialBuilderProperties: Map<String, Value> = mapOf(),
  initialProperties: Map<String, Value> = mapOf(),
) : SourceState(
  sourceId = sourceId,
  sourceType = "geojson",
  builderProperties = initialBuilderProperties.toMutableMap(),
  initialProperties = initialProperties,
  initialGeoJsonData = initialData
) {

  /**
   * Sets GeoJson `data` property as [GeoJSONData].
   *
   * The data will be scheduled and applied on a worker thread and no validation happens synchronously.
   * If [data] is invalid - `MapLoadingError` with `type = metadata` will be invoked.
   */
  public var data: GeoJSONData
    get() = cachedGeoJsonSourceData.value
    set(value) {
      cachedGeoJsonSourceData.value = value
    }

  /**
   * Maximum zoom level at which to create vector tiles (higher means greater detail at high zoom
   * levels).
   */
  public var maxZoom: MaxZoom
    get() = MaxZoom(getBuilderProperty(MaxZoom.NAME) ?: MaxZoom.default.value)
    set(value) {
      setBuilderProperty(MaxZoom.NAME, value.value)
    }

  /**
   * Contains an attribution to be displayed when the map is shown to a user.
   */
  public var attribution: Attribution
    get() = Attribution(getBuilderProperty(Attribution.NAME) ?: Attribution.default.value)
    set(value) {
      setBuilderProperty(Attribution.NAME, value.value)
    }

  /**
   * Size of the tile buffer on each side. A value of 0 produces no buffer. A
   * value of 512 produces a buffer as wide as the tile itself. Larger values produce fewer
   * rendering artifacts near tile edges and slower performance.
   */
  public var buffer: Buffer
    get() = Buffer(getBuilderProperty(Buffer.NAME) ?: Buffer.default.value)
    set(value) {
      setBuilderProperty(Buffer.NAME, value.value)
    }

  /**
   * Douglas-Peucker simplification tolerance (higher means simpler geometries and faster performance).
   */
  public var tolerance: Tolerance
    get() = Tolerance(getBuilderProperty(Tolerance.NAME) ?: Tolerance.default.value)
    set(value) {
      setBuilderProperty(Tolerance.NAME, value.value)
    }

  /**
   * If the data is a collection of point features, setting this to true clusters the points
   * by radius into groups. Cluster groups become new `Point` features in the source with additional properties:
   * - `cluster` Is `true` if the point is a cluster
   * - `cluster_id` A unqiue id for the cluster to be used in conjunction with the
   * [cluster inspection methods](https://www.mapbox.com/mapbox-gl-js/api/#geojsonsource#getclusterexpansionzoom)
   * - `point_count` Number of original points grouped into this cluster
   * - `point_count_abbreviated` An abbreviated point count
   */
  public var cluster: Cluster
    get() = Cluster(getBuilderProperty(Cluster.NAME) ?: Cluster.default.value)
    set(value) {
      setBuilderProperty(Cluster.NAME, value.value)
    }

  /**
   * Radius of each cluster if clustering is enabled. A value of 512 indicates a radius equal
   * to the width of a tile.
   */
  public var clusterRadius: ClusterRadius
    get() = ClusterRadius(getBuilderProperty(ClusterRadius.NAME) ?: ClusterRadius.default.value)
    set(value) {
      setBuilderProperty(ClusterRadius.NAME, value.value)
    }

  /**
   * Max zoom on which to cluster points if clustering is enabled. Defaults to one zoom less
   * than maxzoom (so that last zoom features are not clustered). Clusters are re-evaluated at integer zoom
   * levels so setting clusterMaxZoom to 14 means the clusters will be displayed until z15.
   */
  public var clusterMaxZoom: ClusterMaxZoom
    get() = ClusterMaxZoom(getBuilderProperty(ClusterMaxZoom.NAME) ?: ClusterMaxZoom.default.value)
    set(value) {
      setBuilderProperty(ClusterMaxZoom.NAME, value.value)
    }

  /**
   * An object defining custom properties on the generated clusters if clustering is enabled, aggregating values from
   * clustered points. Has the form `{"property_name": [operator, map_expression]}`. `operator` is any expression function that accepts at
   * least 2 operands (e.g. `"+"` or `"max"`) — it accumulates the property value from clusters/points the
   * cluster contains; `map_expression` produces the value of a single point.
   *
   * Example: `{"sum": ["+", ["get", "scalerank"]]}`.
   *
   * For more advanced use cases, in place of `operator`, you can use a custom reduce expression
   * that references a special `["accumulated"]` value, e.g.:
   * `{"sum": [["+", ["accumulated"], ["get", "sum"]], ["get", "scalerank"]]}`
   */
  public var clusterProperties: ClusterProperties
    get() = ClusterProperties(getBuilderProperty(ClusterProperties.NAME) ?: ClusterProperties.default.value)
    set(value) {
      setBuilderProperty(ClusterProperties.NAME, value.value)
    }

  /**
   * Whether to calculate line distance metrics. This is required for line layers that specify `line-gradient` values.
   */
  public var lineMetrics: LineMetrics
    get() = LineMetrics(getBuilderProperty(LineMetrics.NAME) ?: LineMetrics.default.value)
    set(value) {
      setBuilderProperty(LineMetrics.NAME, value.value)
    }

  /**
   * Whether to generate ids for the geojson features. When enabled, the `feature.id` property will be auto
   * assigned based on its index in the `features` array, over-writing any previous values.
   */
  public var generateId: GenerateId
    get() = GenerateId(getBuilderProperty(GenerateId.NAME) ?: GenerateId.default.value)
    set(value) {
      setBuilderProperty(GenerateId.NAME, value.value)
    }

  /**
   * A property to use as a feature id (for feature state). Either a property name, or
   * an object of the form `{<sourceLayer>: <propertyName>}`.
   */
  public var promoteId: PromoteId
    get() = PromoteId(getBuilderProperty(PromoteId.NAME) ?: PromoteId.default.value)
    set(value) {
      setBuilderProperty(PromoteId.NAME, value.value)
    }

  /**
   * When loading a map, if PrefetchZoomDelta is set to any number greater than 0, the map
   * will first request a tile at zoom level lower than zoom - delta, but so that
   * the zoom level is multiple of delta, in an attempt to display a full map at
   * lower resolution as quick as possible. It will get clamped at the tile source minimum zoom.
   * The default delta is 4.
   */
  public var prefetchZoomDelta: PrefetchZoomDelta
    get() = PrefetchZoomDelta(getProperty(PrefetchZoomDelta.NAME) ?: PrefetchZoomDelta.default.value)
    set(value) {
      setProperty(PrefetchZoomDelta.NAME, value.value)
    }

  /**
   * This property defines a source-specific resource budget, either in tile units or in megabytes. Whenever the
   * tile cache goes over the defined limit, the least recently used tile will be evicted from
   * the in-memory cache. Note that the current implementation does not take into account resources allocated by
   * the visible tiles.
   */
  public var tileCacheBudget: TileCacheBudget
    get() = TileCacheBudget(getProperty(TileCacheBudget.NAME) ?: TileCacheBudget.default.value)
    set(value) {
      setProperty(TileCacheBudget.NAME, value.value)
    }

  /**
   * Public companion object.
   */
  public companion object {
    /**
     * The default saver implementation for [GeoJsonSourceState]
     */
    public val Saver: Saver<GeoJsonSourceState, Holder> = Saver(
      save = { it.save() },
      restore = {
        GeoJsonSourceState(
          sourceId = it.sourcedId,
          initialData = it.geoJSONData,
          initialBuilderProperties = it.builderProperties,
          initialProperties = it.cachedProperties,
        )
      }
    )
  }
}
// End of generated file.