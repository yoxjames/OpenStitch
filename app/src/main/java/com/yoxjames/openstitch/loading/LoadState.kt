package com.yoxjames.openstitch.loading

import com.yoxjames.openstitch.navigation.NavigationScreenState
import kotlinx.coroutines.flow.SharedFlow

/**
 * This is a simple state model to model caching behavior for any object. This is essentially
 * a container state.
 */
sealed interface LoadState

/**
 * The object in question is not loaded.
 */
object NotLoaded : LoadState

/**
 * The object in question has been loaded. The loading time is recorded as well as the
 * ScreenRequest that it goes with. The state is a SharedFlow. The SharedFlow packed into
 * state should have a replay of at least 1 so that the cached value is "replayed" when
 * this Loaded state is accessed. This prevents us from running whatever cold flow
 * was used to initially build the object T.
 */
data class Loaded<T>(
    val loadTime: Long,
    val state: T
) : LoadState

data class ViewScreen(
    val navigationScreenState: NavigationScreenState
)
