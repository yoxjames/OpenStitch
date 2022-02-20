package com.yoxjames.openstitch.ui.core

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yoxjames.openstitch.core.ViewEvent
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState
import com.yoxjames.openstitch.detail.ContentViewState
import com.yoxjames.openstitch.list.ListViewState
import com.yoxjames.openstitch.loading.LoadingViewState
import com.yoxjames.openstitch.ui.*
import org.w3c.dom.Text

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
