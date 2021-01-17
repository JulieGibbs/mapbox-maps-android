package com.mapbox.maps.plugin.delegates

import com.mapbox.maps.*

/**
 * Definition of the feature query delegate. Provide interface to query map's features.
 */
interface MapFeatureQueryDelegate {
  /**
   * Queries the map for rendered features.
   *
   * @param shape Screen pixel coordinates (point, line string or box) to query for rendered features.
   * @param options Options for querying rendered features.
   * @param callback Callback called when the query completes
   */
  fun queryRenderedFeatures(
    shape: List<ScreenCoordinate?>,
    options: RenderedQueryOptions,
    callback: QueryFeaturesCallback
  )

  /**
   * Queries the map for rendered features.
   *
   * @param box Screen box to query for rendered features.
   * @param options Options for querying rendered features.
   * @param callback Callback called when the query completes
   */
  fun queryRenderedFeatures(
    box: ScreenBox,
    options: RenderedQueryOptions,
    callback: QueryFeaturesCallback
  )

  /**
   * Queries the map for rendered features.
   *
   * @param pixel Screen pixel coordinate to query for rendered features.
   * @param options Options for querying rendered features.
   * @param callback Callback called when the query completes
   */
  fun queryRenderedFeatures(
    pixel: ScreenCoordinate,
    options: RenderedQueryOptions,
    callback: QueryFeaturesCallback
  )

  /**
   * Queries the map for source features.
   *
   * @param sourceId Style source identifier used to query for source features.
   * @param options Options for querying source features.
   * @param callback Callback called when the query completes
   */
  fun querySourceFeatures(
    sourceId: String,
    options: SourceQueryOptions,
    callback: QueryFeaturesCallback
  )
}