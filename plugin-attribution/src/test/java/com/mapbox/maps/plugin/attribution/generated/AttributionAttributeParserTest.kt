// This file is generated.

package com.mapbox.maps.plugin.attribution.generated

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AttributionAttributeParserTest {
  private val context: Context = mockk(relaxed = true)

  private val attrs: AttributeSet = mockk(relaxUnitFun = true)

  private val typedArray: TypedArray = mockk(relaxUnitFun = true)

  private val drawable = mockk<Drawable>(relaxed = true)

  @Before
  fun setUp() {
    mockkObject(AttributionAttributeParser::class)
    every { context.obtainStyledAttributes(any(), any(), 0, 0) } returns typedArray
    every { typedArray.getString(any()) } returns "pk.token"
    every { typedArray.getBoolean(any(), any()) } returns true
    every { typedArray.getInt(any(), any()) } returns 2
    every { typedArray.getColor(any(), any()) } returns Color.RED
    every { typedArray.getDimension(any(), any()) } returns 10.0f
    every { typedArray.getFloat(any(), any()) } returns 10.0f
    every { typedArray.getDrawable(any()) } returns drawable
    every { typedArray.hasValue(any()) } returns true
  }

  @After
  fun cleanUp() {
    unmockkAll()
  }

  @Test
  fun enabledTestTrue() {
    every { typedArray.getBoolean(any(), any()) } returns true
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(true, settings.enabled)
  }

  @Test
  fun enabledTestFalse() {
    every { typedArray.getBoolean(any(), any()) } returns false
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(false, settings.enabled)
  }

  @Test
  fun iconColorTest() {
    every { typedArray.getColor(any(), any()) } returns Color.parseColor("#FF1E8CAB")
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(Color.parseColor("#FF1E8CAB"), settings.iconColor)
  }

  @Test
  fun positionTest() {
    every { typedArray.getInt(any(), any()) } returns 100
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(100, settings.position)
  }

  @Test
  fun marginLeftTest() {
    every { typedArray.getDimension(any(), any()) } returns 92f
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(92f, settings.marginLeft)
  }

  @Test
  fun marginTopTest() {
    every { typedArray.getDimension(any(), any()) } returns 4f
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(4f, settings.marginTop)
  }

  @Test
  fun marginRightTest() {
    every { typedArray.getDimension(any(), any()) } returns 4f
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(4f, settings.marginRight)
  }

  @Test
  fun marginBottomTest() {
    every { typedArray.getDimension(any(), any()) } returns 4f
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(4f, settings.marginBottom)
  }

  @Test
  fun clickableTestTrue() {
    every { typedArray.getBoolean(any(), any()) } returns true
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(true, settings.clickable)
  }

  @Test
  fun clickableTestFalse() {
    every { typedArray.getBoolean(any(), any()) } returns false
    val settings = AttributionAttributeParser.parseAttributionSettings(context, attrs, 1.2f)
    assertEquals(false, settings.clickable)
  }
}

// End of generated file.