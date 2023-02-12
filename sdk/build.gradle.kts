import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
  id("com.android.library")
  kotlin("android")
  id("com.jaredsburrows.license")
  id("org.jetbrains.dokka")
  // FIXME https://mapbox.atlassian.net/browse/MAPSAND-794
  //id("com.mapbox.android.sdk.versions")
  id("io.gitlab.arturbosch.detekt").version(Versions.detekt)
}

android {
  compileSdk = AndroidVersions.compileSdkVersion
  defaultConfig {
    minSdk = AndroidVersions.minSdkVersion
    targetSdk = AndroidVersions.targetSdkVersion
    consumerProguardFiles("proguard-rules.pro")
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testInstrumentationRunnerArguments(mapOf("clearPackageData" to "true"))

    if (project.hasProperty("android.injected.invoked.from.ide")) {
      buildConfigField("boolean", "RUN_FROM_IDE", "true")
    } else {
      buildConfigField("boolean", "RUN_FROM_IDE", "false")
    }
  }

  testOptions {
    unitTests.apply {
      isIncludeAndroidResources = true
    }
    animationsDisabled = true
    if (!project.hasProperty("android.injected.invoked.from.ide")) {
      execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
    }
  }

  flavorDimensions.add("version")
  productFlavors {
    val private by creating {
      dimension = "version"
    }
    val public by creating {
      dimension = "version"
      isDefault = true
    }
  }

  sourceSets {
    // limit amount of exposed library resources
    getByName("public").res.srcDirs("src/public/res-public")
    getByName("private").res.srcDirs("src/private/res-public")
  }
}

val buildFromSource: String by project

dependencies {
  api(Dependencies.mapboxBase)
  if (buildFromSource.toBoolean()) {
    api(project(":okhttp"))
  } else {
    api(Dependencies.mapboxOkHttp)
  }

  implementation(Dependencies.mapboxAnnotations)
  api(project(":sdk-base"))
  implementation(project(":module-telemetry"))
  api(project(":extension-style"))
  api(project(":plugin-logo"))
  api(project(":plugin-compass"))
  api(project(":plugin-gestures"))
  api(project(":plugin-attribution"))
  api(project(":plugin-locationcomponent"))
  api(project(":plugin-animation"))
  api(project(":plugin-scalebar"))
  api(project(":plugin-overlay"))
  api(project(":plugin-annotation"))
  api(project(":extension-localization"))
  api(project(":plugin-lifecycle"))
  api(project(":plugin-viewport"))
  compileOnly(Dependencies.asyncInflater)
  implementation(Dependencies.kotlin)
  implementation(Dependencies.androidxCoreKtx)
  implementation(Dependencies.androidxAnnotations)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.mockk)
  testImplementation(Dependencies.androidxTestCore)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.robolectricEgl)
  testImplementation(Dependencies.asyncInflater)
  testImplementation(Dependencies.equalsVerifier)
  testImplementation(Dependencies.androidxTestJUnit)
  debugImplementation(Dependencies.androidxAppCompat)
  androidTestImplementation(Dependencies.androidxTestRunner)
  androidTestImplementation(Dependencies.androidxTestJUnit)
  androidTestImplementation(Dependencies.androidxRules)
  androidTestImplementation(Dependencies.androidxJUnitTestRules)
  androidTestImplementation(Dependencies.androidxEspresso)
  androidTestImplementation(Dependencies.androidxUiAutomator)
  androidTestImplementation(Dependencies.coroutines)
  androidTestUtil(Dependencies.androidxOrchestrator)
  detektPlugins(Dependencies.detektFormatting)
}

// let's register different Dokka Javadoc tasks per flavor
android.productFlavors.all {
  val flavor = name
  tasks.register("${flavor}ReleaseDokkaJavadoc", DokkaTask::class.java) {
    // We want to generate Javadoc so we copy the `dokkaJavadoc` task plugins dependencies in order
    // to generate documentation in Javadoc format
    val dokkaJavadocTask = tasks.findByName("dokkaJavadoc") as DokkaTask
    plugins.dependencies.addAll(dokkaJavadocTask.plugins.allDependencies)
    // Make sure we disable all the source sets not related to this flavour (and only for release build)
    dokkaSourceSets.configureEach {
      if (name != "main" && name != flavor && name != "${flavor}Release") {
        suppress.set(true)
      }
      reportUndocumented.set(true)
      failOnWarning.set(true)
      if (buildFromSource.toBoolean()) {
        File("upstream-api-doc-list.txt").forEachLine {
          if (!it.startsWith("//")) {
            sourceRoots.from(file("../../$it"))
          }
        }
      }
    }
  }
}

project.apply {
  from("$rootDir/gradle/ktlint.gradle")
  from("$rootDir/gradle/lint.gradle")
  from("$rootDir/gradle/jacoco.gradle")
  from("$rootDir/gradle/sdk-registry.gradle")
  from("$rootDir/gradle/track-public-apis.gradle")
  from("$rootDir/gradle/detekt.gradle")
  from("$rootDir/gradle/dependency-updates.gradle")
}
