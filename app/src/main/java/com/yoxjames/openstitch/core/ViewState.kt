package com.yoxjames.openstitch.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emitAll

interface ViewState

interface State

interface ViewEvent

fun interface ViewEventListener <VE : ViewEvent> {
    suspend fun onEvent(viewEvent: VE)
}

class ViewEventFlowAdapter <VE: ViewEvent> : ViewEventListener<VE> {
    private val _viewEvents = MutableSharedFlow<VE>()
    val flow = _viewEvents.asSharedFlow()

    override suspend fun onEvent(viewEvent: VE) {
        _viewEvents.emit(viewEvent)
    }
}

class ConnectableFlowHolder<T> {
    private val sharedFlow = MutableSharedFlow<T>()
    val flow = sharedFlow.asSharedFlow()

    suspend fun connectFlow(flow: Flow<T>) {
        sharedFlow.emitAll(flow)
    }
}



