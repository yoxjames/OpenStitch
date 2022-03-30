package com.yoxjames.openstitch.pattern.vm.list

import androidx.compose.material.ExperimentalMaterialApi
import com.yoxjames.openstitch.core.ViewEvent
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

object PatternListResumeViewEvent : PatternListScreenViewEvent

fun SearchState.mapToTopBarViewState() = when (this) {
    is InactiveSearchState -> DefaultTopBarViewState(isSearchAvailable = true, isBackAvailable = false)
    is EnteredSearchState, is FocusedSearchState, is TypingSearchState -> SearchTopBarViewState(SearchViewStateMapper(this))
}
