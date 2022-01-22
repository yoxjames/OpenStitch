package com.yoxjames.openstitch.ui.core

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEvent
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState
import com.yoxjames.openstitch.detail.ContentViewState
import com.yoxjames.openstitch.list.ListViewState
import com.yoxjames.openstitch.loading.LoadingViewState
import com.yoxjames.openstitch.ui.DefaultTopBarViewState
import com.yoxjames.openstitch.ui.TopBarViewState

sealed interface ScreenViewState : ViewState {
    @Composable
    fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>)
}

object LoadingViewState : ScreenViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>) {
        OpenStitchScaffold(viewEventListener, DefaultTopBarViewState(), LoadingViewState(true)) { }
    }
}

data class ListScreenViewState(
    val topBarViewState: TopBarViewState,
    val listViewState: ListViewState,
    val loadingViewState: LoadingViewState
) : ScreenViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>) {
        OpenStitchScaffold(
            viewEventListener = viewEventListener,
            topBarViewState = topBarViewState,
            loadingViewState = loadingViewState
        ) {
            listViewState.Composable(viewEventListener = { viewEventListener.onEvent(it) })
        }
    }
}

data class DetailScreenViewState(
    val topBarViewState: TopBarViewState,
    val contentViewState: ContentViewState,
    val loadingViewState: LoadingViewState
) : ScreenViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>) {
        OpenStitchScaffold(
            viewEventListener = viewEventListener,
            topBarViewState = topBarViewState,
            loadingViewState = loadingViewState
        ) {
            contentViewState.Composable(viewEventListener = viewEventListener)
        }
    }
}

@Composable
fun OpenStitchScaffold(
    viewEventListener: ViewEventListener<ScreenViewEvent>,
    topBarViewState: TopBarViewState,
    loadingViewState: LoadingViewState,
    Content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = { topBarViewState.Composable { viewEventListener.onEvent(it) } },
        content = { Content(); loadingViewState.Composable() }
    )
}

sealed interface ScreenViewEvent : ViewEvent

interface TopBarScreenViewEvent : ScreenViewEvent

interface ListScreenViewEvent : ScreenViewEvent

object BackPushed : ScreenViewEvent
