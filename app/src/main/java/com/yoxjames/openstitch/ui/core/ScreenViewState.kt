package com.yoxjames.openstitch.ui.core

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEvent
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.loading.LoadingViewState
import com.yoxjames.openstitch.ui.TopBarViewEvent
import com.yoxjames.openstitch.ui.TopBarViewState

@Composable
fun OpenStitchScaffold(
    onTopBarViewEvent: ViewEventListener<TopBarViewEvent>,
    topBarViewState: TopBarViewState,
    loadingViewState: LoadingViewState,
    Content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = { topBarViewState.Composable(onTopBarViewEvent) },
        content = { Content(); loadingViewState.Composable() }
    )
}

sealed interface ScreenViewEvent : ViewEvent

interface TopBarScreenViewEvent : ScreenViewEvent

object BackPushed : ScreenViewEvent

object ViewScreen : ScreenViewEvent
