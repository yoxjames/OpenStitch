package com.yoxjames.openstitch.ui.core

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEvent
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState
import com.yoxjames.openstitch.detail.ContentViewState
import com.yoxjames.openstitch.list.ListViewState
import com.yoxjames.openstitch.loading.LoadingViewState
import com.yoxjames.openstitch.ui.BottomBarViewState
import com.yoxjames.openstitch.ui.DefaultTopBarViewState
import com.yoxjames.openstitch.ui.NoBottomBarViewState
import com.yoxjames.openstitch.ui.TopBarViewState

data class ScreenStates(
    val patternListState: LazyListState
)

sealed interface ScreenViewState : ViewState {
    @Composable
    fun Composable(
        screenStates: ScreenStates,
        viewEventListener: ViewEventListener<ScreenViewEvent>
    )
}

object LoadingViewState : ScreenViewState {
    @Composable
    override fun Composable(screenStates: ScreenStates, viewEventListener: ViewEventListener<ScreenViewEvent>) {
        OpenStitchScaffold(viewEventListener, DefaultTopBarViewState(), NoBottomBarViewState, LoadingViewState(true)) { }
    }
}

data class ListScreenViewState(
    val topBarViewState: TopBarViewState,
    val bottomBarViewState: BottomBarViewState,
    val listViewState: ListViewState,
    val loadingViewState: LoadingViewState
) : ScreenViewState {
    @Composable
    override fun Composable(
        screenStates: ScreenStates,
        viewEventListener: ViewEventListener<ScreenViewEvent>
    ) {
        OpenStitchScaffold(
            viewEventListener = viewEventListener,
            topBarViewState = topBarViewState,
            bottomBarViewState = bottomBarViewState,
            loadingViewState = loadingViewState
        ) {
            listViewState.Composable(
                scrollState = screenStates.patternListState,
                viewEventListener = { viewEventListener.onEvent(it) }
            )
        }
    }
}

data class DetailScreenViewState(
    val topBarViewState: TopBarViewState,
    val bottomBarViewState: BottomBarViewState,
    val contentViewState: ContentViewState,
    val loadingViewState: LoadingViewState
) : ScreenViewState {
    @Composable
    override fun Composable(
        screenStates: ScreenStates,
        viewEventListener: ViewEventListener<ScreenViewEvent>
    ) {
        OpenStitchScaffold(
            viewEventListener = viewEventListener,
            topBarViewState = topBarViewState,
            bottomBarViewState = bottomBarViewState,
            loadingViewState = loadingViewState,
        ) {
            contentViewState.Composable(viewEventListener = viewEventListener)
        }
    }
}

@Composable
fun OpenStitchScaffold(
    viewEventListener: ViewEventListener<ScreenViewEvent>,
    topBarViewState: TopBarViewState,
    bottomBarViewState: BottomBarViewState,
    loadingViewState: LoadingViewState,
    Content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = { topBarViewState.Composable { viewEventListener.onEvent(it) } },
        bottomBar = { bottomBarViewState.Composable { viewEventListener.onEvent(it) } },
        content = {
            Content()
            loadingViewState.Composable()
        }
    )
}

sealed interface ScreenViewEvent : ViewEvent

interface TopBarScreenViewEvent : ScreenViewEvent

interface BottomBarScreenViewEvent : ScreenViewEvent

interface ListScreenViewEvent : ScreenViewEvent

object BackPushed : ScreenViewEvent

object ViewScreen : ScreenViewEvent
