package com.yoxjames.openstitch

import com.yoxjames.openstitch.ui.core.LoadingViewState
import com.yoxjames.openstitch.ui.core.ScreenViewState
import com.yoxjames.openstitch.core.State
import com.yoxjames.openstitch.detail.ContentState
import com.yoxjames.openstitch.list.ListState
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.search.InactiveSearchState
import com.yoxjames.openstitch.search.SearchState
import com.yoxjames.openstitch.ui.DefaultTopBarViewState
import com.yoxjames.openstitch.ui.SearchTopBarViewState
import com.yoxjames.openstitch.ui.SearchViewState
import com.yoxjames.openstitch.ui.core.DetailScreenViewState
import com.yoxjames.openstitch.ui.core.ListScreenViewState

sealed interface OpenStitchState : State {
    val viewState: ScreenViewState
}

object LoadingScreenState : OpenStitchState {
    override val viewState: ScreenViewState = LoadingViewState
}

data class ListScreenState(
    val searchState: SearchState,
    val listState: ListState,
    val loadingState: LoadingState
) : OpenStitchState {
    override val viewState: ScreenViewState = ListScreenViewState(
        topBarViewState = when (searchState !is InactiveSearchState) {
            true -> SearchTopBarViewState(SearchViewState("Search Patterns"))
            false -> DefaultTopBarViewState(true)
        },
        listViewState = listState.asViewState(),
        loadingViewState = loadingState.viewState
    )
}

data class DetailScreenState(
    val contentState: ContentState,
    val loadingState: LoadingState
) : OpenStitchState {
    override val viewState = DetailScreenViewState(
        topBarViewState = DefaultTopBarViewState(),
        contentViewState = contentState.viewState,
        loadingViewState = loadingState.viewState
    )
}