package com.yoxjames.openstitch.core

/**
 * This indicates a type of class that is designed to be rendered
 * as a View. Essentially a state that closely resembles what a user could see
 * and often would be implemented with a @Composable function.
 */
interface ViewState

/**
 * A event that originates from the View.
 */
interface ViewEvent

fun interface ViewEventListener <VE : ViewEvent> {
    suspend fun onEvent(viewEvent: VE)
}
