package com.yoxjames.openstitch.pattern.vm

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.yoxjames.openstitch.core.ViewEvent
import com.yoxjames.openstitch.list.Composable
import com.yoxjames.openstitch.list.StatefulListItemViewEvent
import com.yoxjames.openstitch.pattern.state.PatternListState
import com.yoxjames.openstitch.search.EnteredSearchState
import com.yoxjames.openstitch.search.FocusedSearchState
import com.yoxjames.openstitch.search.InactiveSearchState
import com.yoxjames.openstitch.search.SearchState
import com.yoxjames.openstitch.search.SearchViewStateMapper
import com.yoxjames.openstitch.search.TypingSearchState
import com.yoxjames.openstitch.ui.DefaultTopBarViewState
import com.yoxjames.openstitch.ui.SearchTopBarViewState
import com.yoxjames.openstitch.ui.TopBarViewEvent
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold
import kotlinx.coroutines.flow.StateFlow

@ExperimentalMaterialApi
interface PatternListViewModel {
    val state: StateFlow<PatternListScreenState>
    suspend fun emitViewEvent(viewEvent: PatternListScreenViewEvent)
    suspend fun start()
}

@ExperimentalMaterialApi
data class PatternListScreenState(
    val searchState: SearchState,
    val patternListState: PatternListState
)

sealed interface PatternListScreenViewEvent : ViewEvent

@JvmInline
value class PatternListTopBarViewEvent(
    val event: TopBarViewEvent
) : PatternListScreenViewEvent

@JvmInline
value class PatternListViewEvent(
    val event: StatefulListItemViewEvent
) : PatternListScreenViewEvent

fun SearchState.mapToTopBarViewState() = when (this) {
    is InactiveSearchState -> DefaultTopBarViewState(isSearchAvailable = true, isBackAvailable = false)
    is EnteredSearchState, is FocusedSearchState, is TypingSearchState -> SearchTopBarViewState(SearchViewStateMapper(this))
}

@ExperimentalMaterialApi
@Composable
fun PatternListView(listState: LazyListState, patternListViewModel: PatternListViewModel) {
    val state by patternListViewModel.state.collectAsState()
    with(state) {
        OpenStitchScaffold(
            onTopBarViewEvent = { patternListViewModel.emitViewEvent(PatternListTopBarViewEvent(it)) },
            topBarViewState = searchState.mapToTopBarViewState(),
            loadingViewState = patternListState.loadingState.viewState
        ) {
            patternListState.listState.Composable(
                scrollState = listState,
                viewEventListener = { patternListViewModel.emitViewEvent(PatternListViewEvent(it)) }
            )
        }
    }
    LaunchedEffect(true) {
        patternListViewModel.start()
    }
}
