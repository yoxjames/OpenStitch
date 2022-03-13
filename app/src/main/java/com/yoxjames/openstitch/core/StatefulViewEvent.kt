package com.yoxjames.openstitch.core

interface StatefulViewEvent<VE, S> : ViewEvent {
    val viewEvent: VE
    val state: S
}
