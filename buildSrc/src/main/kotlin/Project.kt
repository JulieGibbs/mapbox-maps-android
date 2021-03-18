object AndroidVersions {
  const val minSdkVersion = 21
  const val targetSdkVersion = 28
  const val compileSdkVersion = 28
}

object Plugins {
  const val android = "com.android.tools.build:gradle:${Versions.pluginAndroidGradle}"
  const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.pluginKotlin}"
  const val jacoco = "com.hiya:jacoco-android:${Versions.pluginJacoco}"
  const val license = "com.jaredsburrows:gradle-license-plugin:${Versions.pluginLicense}"
  const val androidPublish = "digital.wup:android-maven-publish:${Versions.pluginMavenPublish}"
  const val mapboxAccessToken = "com.mapbox.gradle.plugins:access-token:${Versions.mapboxAccessToken}"
  const val mapboxSdkRegistry = "com.mapbox.gradle.plugins:sdk-registry:${Versions.mapboxSdkRegistry}"
}

object Dependencies {
  const val mapboxBase = "com.mapbox.base:common:${Versions.mapboxBase}"
  const val mapboxAnnotations = "com.mapbox.base:annotations:${Versions.mapboxBase}"
  const val mapboxAnnotationsProcessor = "com.mapbox.base:annotations-processor:${Versions.mapboxBase}"
  const val mapboxJavaGeoJSON = "com.mapbox.mapboxsdk:mapbox-sdk-geojson:${Versions.mapboxJavaServices}"
  const val mapboxServices = "com.mapbox.mapboxsdk:mapbox-sdk-services:${Versions.mapboxJavaServices}"
  const val mapboxGlNative = "com.mapbox.maps:android-core:${Versions.mapboxGlNative}"
  const val mapboxCoreCommon = "com.mapbox.common:common:${Versions.mapboxCommon}"
  const val mapboxOkHttp = "com.mapbox.common:okhttp:${Versions.mapboxCommon}"
  const val mapboxAndroidCore = "com.mapbox.mapboxsdk:mapbox-android-core:${Versions.mapboxAndroidCore}"
  const val mapboxAndroidTelemetry = "com.mapbox.mapboxsdk:mapbox-android-telemetry:${Versions.mapboxAndroidTelemetry}"
  const val mapboxJavaTurf = "com.mapbox.mapboxsdk:mapbox-sdk-turf:${Versions.mapboxJavaServices}"
  const val mapboxGestures = "com.mapbox.mapboxsdk:mapbox-android-gestures:${Versions.mapboxGestures}"
  const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppcompat}"
  const val androidxRecyclerView = "androidx.recyclerview:recyclerview:${Versions.androidxRecyclerView}"
  const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.androidxCore}"
  const val androidxAnnotations = "androidx.annotation:annotation:${Versions.androidxAnnotation}"
  const val androidxInterpolators = "androidx.interpolator:interpolator:${Versions.androidxInterpolator}"
  const val androidxConstraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.androidxConstraintLayout}"
  const val androidxEspresso = "androidx.test.espresso:espresso-core:${Versions.androidxEspresso}"
  const val androidxTestJUnit = "androidx.test.ext:junit:${Versions.androidxJUnit}"
  const val androidxRules = "androidx.test:rules:${Versions.androidxTest}"
  const val androidxJUnitTestRules = "androidx.test:rules:${Versions.androidxTest}"
  const val androidxTestRunner = "androidx.test:runner:${Versions.androidxTest}"
  const val androidxTestCore = "androidx.test:core:${Versions.androidxTest}"
  const val androidxUiAutomator = "androidx.test.uiautomator:uiautomator:${Versions.androidxUiAutomator}"
  const val androidxOrchestrator = "androidx.test:orchestrator:${Versions.androidxTest}"
  const val androidxMultidex = "androidx.multidex:multidex:${Versions.androidxMultidex}"
  const val googleMaterialDesign = "com.google.android.material:material:${Versions.materialDesign}"
  const val squareLeakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.squareLeakCanary}"
  const val squareRetrofit = "com.squareup.retrofit2:retrofit:${Versions.squareRetrofit}"
  const val squareRetrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.squareRetrofit}"
  const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.pluginKotlin}"
  const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
  const val junit = "junit:junit:${Versions.junit}"
  const val mockk = "io.mockk:mockk:${Versions.mockk}"
  const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
  const val robolectricEgl = "org.khronos:opengl-api:${Versions.robolectricEgl}"
}

object Versions {
  const val pluginAndroidGradle = "4.0.1"
  const val pluginKotlin = "1.4.0"
  const val pluginLicense = "0.8.5"
  const val pluginDokka =  "1.4.10"
  const val pluginJacoco = "0.2"
  const val pluginMavenPublish = "3.6.2"
  const val mapboxAccessToken="0.2.1"
  const val mapboxSdkRegistry="0.4.0"
  const val mapboxGestures = "0.7.0"
  const val mapboxJavaServices = "5.4.1"
  const val mapboxBase = "0.5.0"
  const val mapboxGlNative = "10.0.0-beta.17"
  const val mapboxCommon = "10.0.2"
  const val mapboxAndroidCore = "3.1.1"
  const val mapboxAndroidTelemetry = "6.2.2"
  const val androidxCore = "1.3.1"
  const val androidxAnnotation = "1.1.0"
  const val androidxAppcompat = "1.2.0"
  const val androidxTest = "1.3.0"
  const val androidxConstraintLayout = "2.0.0"
  const val androidxEspresso = "3.3.0"
  const val androidxJUnit = "1.1.2"
  const val androidxUiAutomator = "2.2.0"
  const val androidxRecyclerView = "1.1.0"
  const val androidxInterpolator="1.0.0"
  const val androidxMultidex = "2.0.1"
  const val squareRetrofit="2.9.0"
  const val squareLeakCanary = "2.4"
  const val materialDesign = "1.2.0"
  const val kotlinCoroutines = "1.3.9"
  const val junit = "4.12"
  const val mockk = "1.9.3"
  const val robolectric = "4.3.1"
  const val robolectricEgl = "gl1.1-android-2.1_r1"
}
