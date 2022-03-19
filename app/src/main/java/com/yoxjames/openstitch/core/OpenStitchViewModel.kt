package com.yoxjames.openstitch.core

import kotlinx.coroutines.flow.StateFlow

interface OpenStitchViewModel<ScreenState, ViewEvent> {
    val state: StateFlow<ScreenState>
    suspend fun emitViewEvent(viewEvent: ViewEvent)
    suspend fun start()
}
