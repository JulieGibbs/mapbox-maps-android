package com.mapbox.maps.extension.compose.animation.camera

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.mapbox.maps.CameraState
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.animateDouble
import com.mapbox.maps.extension.compose.animation.animateDoubleAsState
import com.mapbox.maps.extension.compose.animation.defaultDoubleAnimation

/**
 * Fire-and-forget animation function for Bearing as [Double]. When the provided [targetValue] is changed, the
 * animation will run automatically. If there is already an animation in-flight when [targetValue]
 * changes, the on-going animation will adjust course to animate towards the new target value.
 *
 * [animateBearingAsState] returns a [State] object. The value of the state object will continuously
 * be updated by the animation until the animation finishes.
 *
 * Note, [animateBearingAsState] cannot be canceled/stopped without removing this composable function
 * from the tree. See [Animatable] for cancelable animations.
 *
 * @param targetValue Target value of the animation
 * @param animationSpec The animation that will be used to change the value through time. [tween] with
 *                      1000 ms duration and [LinearOutSlowInEasing] will be used by default.
 * @param visibilityThreshold An optional threshold for deciding when the animation value is
 *                            considered close enough to the targetValue, defaults to 0.01.
 * @param label An optional label to differentiate from other animations in Android Studio.
 * @param finishedListener An optional end listener to get notified when the animation is finished.
 * @return A [State] object, the value of which is updated by animation.
 */
@Composable
@MapboxExperimental
public fun animateBearingAsState(
  targetValue: Double,
  animationSpec: AnimationSpec<Double> = defaultDoubleAnimation,
  visibilityThreshold: Double = 0.01,
  label: String = "CameraBearingAnimation",
  finishedListener: ((Double) -> Unit)? = null
): State<Double> {
  return animateDoubleAsState(
    targetValue = targetValue,
    animationSpec = animationSpec,
    visibilityThreshold = visibilityThreshold,
    label = label,
    finishedListener = finishedListener
  )
}

/**
 * Creates a Bearing animation as a part of the given [Transition]. This means the states
 * of this animation will be managed by the [Transition].
 *
 * [targetValueByState] is used as a mapping from a target state to the target value of this
 * animation. [Transition] will be using this mapping to determine what value to target this
 * animation towards. __Note__ that [targetValueByState] is a composable function. This means the
 * mapping function could access states, CompositionLocals, themes, etc. If the targetValue changes
 * outside of a [Transition] run (i.e. when the [Transition] already reached its targetState), the
 * [Transition] will start running again to ensure this animation reaches its new target smoothly.
 *
 * An optional [transitionSpec] can be provided to specify (potentially different) animation for
 * each pair of initialState and targetState. [FiniteAnimationSpec] includes any non-infinite
 * animation, such as [tween], [spring], [keyframes] and even [repeatable], but not
 * [infiniteRepeatable]. By default, [transitionSpec] uses a [tween] animation with 1000 millisecond
 * duration and [LinearOutSlowInEasing] for all transition destinations.
 *
 * [label] is used to differentiate from other animations in the same transition in Android Studio.
 *
 * @return A [State] object, the value of which is updated by animation
 * @see updateTransition
 * @see Transition.animateValue
 */
@Composable
@MapboxExperimental
public inline fun Transition<CameraState>.animateBearing(
  noinline transitionSpec:
  @Composable Transition.Segment<CameraState>.() -> FiniteAnimationSpec<Double> = { defaultDoubleAnimation },
  label: String = "CameraBearingAnimation",
  targetValueByState: @Composable (state: CameraState) -> Double = { it.bearing }
): State<Double> = animateDouble(transitionSpec, label, targetValueByState)