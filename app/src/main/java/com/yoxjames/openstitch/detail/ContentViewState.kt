package com.yoxjames.openstitch.detail

import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.ui.core.ScreenViewEvent

interface ContentState {
    val viewState: ContentViewState
}

interface ContentViewState {
    @Composable
    fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>)
}

object EmptyContentViewState : ContentViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>) = Unit
}