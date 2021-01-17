// This file is generated.

package com.mapbox.maps.testapp.style.sources.generated

import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mapbox.maps.extension.style.expressions.dsl.generated.*
import com.mapbox.maps.extension.style.sources.TileSet
import com.mapbox.maps.extension.style.sources.generated.*
import com.mapbox.maps.testapp.style.BaseStyleTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Basic smoke tests for RasterSource.
 */
@RunWith(AndroidJUnit4::class)
class RasterSourceTest : BaseStyleTest() {

  @Test
  @UiThreadTest
  fun urlTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      url("abc")
    }
    setupSource(testSource)
    assertEquals("abc", testSource.url)
  }

  @Test
  @UiThreadTest
  fun urlAfterBindTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
    }
    setupSource(testSource)
    testSource.url("abc")
    assertEquals("abc", testSource.url)
  }

  @Test
  @UiThreadTest
  fun urlAsExpressionTest() {
    val expression = literal("abc")
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      url(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.urlAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  @Ignore("https://github.com/mapbox/mapbox-maps-android/issues/499")
  fun tilesTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      tiles(listOf("a", "b", "c"))
    }
    setupSource(testSource)
    assertEquals(listOf("a", "b", "c"), testSource.tiles)
  }

  @Test
  @UiThreadTest
  fun tilesAfterBindTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
    }
    setupSource(testSource)
    testSource.tiles(listOf("a", "b", "c"))
    assertEquals(listOf("a", "b", "c"), testSource.tiles)
  }

  @Test
  @UiThreadTest
  @Ignore("https://github.com/mapbox/mapbox-maps-android/issues/499")
  fun tilesAsExpressionTest() {
    val expression = literal(listOf("a", "b", "c"))
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      tiles(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.tilesAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  @Ignore("https://github.com/mapbox/mapbox-maps-android/issues/499")
  fun boundsTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      bounds(listOf(0.0, 1.0, 2.0, 3.0))
    }
    setupSource(testSource)
    assertEquals(listOf(0.0, 1.0, 2.0, 3.0), testSource.bounds)
  }

  @Test
  @UiThreadTest
  @Ignore("https://github.com/mapbox/mapbox-maps-android/issues/499")
  fun boundsAsExpressionTest() {
    val expression = literal(listOf(0.0, 1.0, 2.0, 3.0))
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      bounds(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.boundsAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  fun minzoomTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      minzoom(1L)
    }
    setupSource(testSource)
    assertEquals(1L, testSource.minzoom)
  }

  @Test
  @UiThreadTest
  fun minzoomAfterBindTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
    }
    setupSource(testSource)
    testSource.minzoom(1L)
    assertEquals(1L, testSource.minzoom)
  }

  @Test
  @UiThreadTest
  fun minzoomAsExpressionTest() {
    val expression = literal(1L)
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      minzoom(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.minzoomAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  fun maxzoomTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      maxzoom(1L)
    }
    setupSource(testSource)
    assertEquals(1L, testSource.maxzoom)
  }

  @Test
  @UiThreadTest
  fun maxzoomAfterBindTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
    }
    setupSource(testSource)
    testSource.maxzoom(1L)
    assertEquals(1L, testSource.maxzoom)
  }

  @Test
  @UiThreadTest
  fun maxzoomAsExpressionTest() {
    val expression = literal(1L)
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      maxzoom(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.maxzoomAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  fun tileSizeTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      tileSize(1L)
    }
    setupSource(testSource)
    assertEquals(1L, testSource.tileSize)
  }

  @Test
  @UiThreadTest
  fun tileSizeAsExpressionTest() {
    val expression = literal(1L)
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      tileSize(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.tileSizeAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  @Ignore("https://github.com/mapbox/mapbox-maps-android/issues/499")
  fun schemeTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      scheme(Scheme.XYZ)
    }
    setupSource(testSource)
    assertEquals(Scheme.XYZ, testSource.scheme)
  }

  @Test
  @UiThreadTest
  @Ignore("https://github.com/mapbox/mapbox-maps-android/issues/499")
  fun schemeAsExpressionTest() {
    val expression = literal("xyz")
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      scheme(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.schemeAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  @Ignore("https://github.com/mapbox/mapbox-maps-android/issues/499")
  fun attributionTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      attribution("abc")
    }
    setupSource(testSource)
    assertEquals("abc", testSource.attribution)
  }

  @Test
  @UiThreadTest
  @Ignore("https://github.com/mapbox/mapbox-maps-android/issues/499")
  fun attributionAsExpressionTest() {
    val expression = literal("abc")
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      attribution(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.attributionAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  fun volatileTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      volatile(true)
    }
    setupSource(testSource)
    assertEquals(true, testSource.volatile)
  }

  @Test
  @UiThreadTest
  fun volatileAfterBindTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
    }
    setupSource(testSource)
    testSource.volatile(true)
    assertEquals(true, testSource.volatile)
  }

  @Test
  @UiThreadTest
  fun volatileAsExpressionTest() {
    val expression = literal(true)
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      volatile(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.volatileAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  fun prefetchZoomDeltaTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      prefetchZoomDelta(1L)
    }
    setupSource(testSource)
    assertEquals(1L, testSource.prefetchZoomDelta)
  }

  @Test
  @UiThreadTest
  fun prefetchZoomDeltaAfterBindTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
    }
    setupSource(testSource)
    testSource.prefetchZoomDelta(1L)
    assertEquals(1L, testSource.prefetchZoomDelta)
  }

  @Test
  @UiThreadTest
  fun prefetchZoomDeltaAsExpressionTest() {
    val expression = literal(1L)
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      prefetchZoomDelta(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.prefetchZoomDeltaAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  fun minimumTileUpdateIntervalTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      minimumTileUpdateInterval(1.0)
    }
    setupSource(testSource)
    assertEquals(1.0, testSource.minimumTileUpdateInterval)
  }

  @Test
  @UiThreadTest
  fun minimumTileUpdateIntervalAfterBindTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
    }
    setupSource(testSource)
    testSource.minimumTileUpdateInterval(1.0)
    assertEquals(1.0, testSource.minimumTileUpdateInterval)
  }

  @Test
  @UiThreadTest
  fun minimumTileUpdateIntervalAsExpressionTest() {
    val expression = literal(1.0)
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      minimumTileUpdateInterval(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.minimumTileUpdateIntervalAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  fun maxOverscaleFactorForParentTilesTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      maxOverscaleFactorForParentTiles(1L)
    }
    setupSource(testSource)
    assertEquals(1L, testSource.maxOverscaleFactorForParentTiles)
  }

  @Test
  @UiThreadTest
  fun maxOverscaleFactorForParentTilesAfterBindTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
    }
    setupSource(testSource)
    testSource.maxOverscaleFactorForParentTiles(1L)
    assertEquals(1L, testSource.maxOverscaleFactorForParentTiles)
  }

  @Test
  @UiThreadTest
  fun maxOverscaleFactorForParentTilesAsExpressionTest() {
    val expression = literal(1L)
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      maxOverscaleFactorForParentTiles(expression)
    }
    setupSource(testSource)
    assertEquals(expression.toString(), testSource.maxOverscaleFactorForParentTilesAsExpression?.toString())
  }

  @Test
  @UiThreadTest
  fun tileSetTest() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      tileSet(TileSet.Builder("abc", listOf("a", "b", "c")).build())
    }
    setupSource(testSource)
  }

  @Test
  @UiThreadTest
  fun tileSetTestDsl() {
    val testSource = rasterSource("testId") {
      url(TEST_URI)
      tileSet("abc", listOf("a", "b", "c")) {
        attribution("testattribution")
      }
    }
    setupSource(testSource)
  }

  // Default source properties getter tests

  @Test
  @UiThreadTest
  fun defaultSourcePropertiesTest() {
    assertNotNull("defaultMinzoom should not be null", RasterSource.defaultMinzoom)
    assertNotNull("defaultMinzoomAsExpression should not be null", RasterSource.defaultMinzoomAsExpression)
    assertNotNull("defaultMaxzoom should not be null", RasterSource.defaultMaxzoom)
    assertNotNull("defaultMaxzoomAsExpression should not be null", RasterSource.defaultMaxzoomAsExpression)
    assertNotNull("defaultScheme should not be null", RasterSource.defaultScheme)
    assertNotNull("defaultSchemeAsExpression should not be null", RasterSource.defaultSchemeAsExpression)
    assertNotNull("defaultVolatile should not be null", RasterSource.defaultVolatile)
    assertNotNull("defaultVolatileAsExpression should not be null", RasterSource.defaultVolatileAsExpression)
    assertNotNull("defaultPrefetchZoomDelta should not be null", RasterSource.defaultPrefetchZoomDelta)
    assertNotNull("defaultPrefetchZoomDeltaAsExpression should not be null", RasterSource.defaultPrefetchZoomDeltaAsExpression)
    assertNotNull("defaultMinimumTileUpdateInterval should not be null", RasterSource.defaultMinimumTileUpdateInterval)
    assertNotNull("defaultMinimumTileUpdateIntervalAsExpression should not be null", RasterSource.defaultMinimumTileUpdateIntervalAsExpression)
  }

  companion object {
    private const val TEST_URI = "https://raw.githubusercontent.com/mapbox/mapbox-gl-native-android/master/MapboxGLAndroidSDKTestApp/src/main/assets/earthquakes.geojson"
  }
}

// End of generated file.