package com.yoxjames.openstitch

import com.yoxjames.openstitch.detail.ContentState
import com.yoxjames.openstitch.list.ListState
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.search.EnteredSearchState
import com.yoxjames.openstitch.search.InactiveSearchState
import com.yoxjames.openstitch.search.SearchState
import com.yoxjames.openstitch.ui.BottomBarViewState
import com.yoxjames.openstitch.ui.DefaultBottomBarViewState
import com.yoxjames.openstitch.ui.DefaultTopBarViewState
import com.yoxjames.openstitch.ui.SearchTopBarViewState
import com.yoxjames.openstitch.ui.SearchViewState
import com.yoxjames.openstitch.ui.core.DetailScreenViewState
import com.yoxjames.openstitch.ui.core.ListScreenViewState
import com.yoxjames.openstitch.ui.core.LoadingViewState
import com.yoxjames.openstitch.ui.core.ScreenViewState

sealed interface OpenStitchState {
    val viewState: ScreenViewState
}

object LoadingScreenState : OpenStitchState {
    override val viewState: ScreenViewState = LoadingViewState
}

data class ListScreenState(
    val searchState: SearchState,
    val listState: ListState,
    val bottomBarViewState: BottomBarViewState,
    val loadingState: LoadingState,
    val navigationState: NavigationState
) : OpenStitchState {
    override val viewState: ScreenViewState = ListScreenViewState(
        topBarViewState = when (searchState !is InactiveSearchState) {
            true -> SearchTopBarViewState(
                SearchViewState(
                    hint = "Search Patterns",
                    text = if (searchState is EnteredSearchState) searchState.searchText else ""
                )
            )
            false -> DefaultTopBarViewState(
                isSearchAvailable = true,
                isBackAvailable = navigationState.isBackAvailable
            )
        },
        bottomBarViewState = bottomBarViewState,
        listViewState = listState.asViewState(),
        loadingViewState = loadingState.viewState
    )
}

data class DetailScreenState(
    val contentState: ContentState,
    val loadingState: LoadingState,
    val navigationState: NavigationState
) : OpenStitchState {
    override val viewState = DetailScreenViewState(
        topBarViewState = DefaultTopBarViewState(
            isSearchAvailable = false,
            isBackAvailable = navigationState.isBackAvailable
        ),
        bottomBarViewState = DefaultBottomBarViewState,
        contentViewState = contentState.viewState,
        loadingViewState = loadingState.viewState
    )
}
