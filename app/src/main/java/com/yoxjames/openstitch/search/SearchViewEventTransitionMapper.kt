package com.yoxjames.openstitch.search

import com.yoxjames.openstitch.ui.SearchBackClick
import com.yoxjames.openstitch.ui.SearchClick
import com.yoxjames.openstitch.ui.SearchEntered
import com.yoxjames.openstitch.ui.SearchTextChanged
import com.yoxjames.openstitch.ui.SearchViewEvent
import com.yoxjames.openstitch.ui.TopBarBackClick
import com.yoxjames.openstitch.ui.TopBarSearchViewEvent
import com.yoxjames.openstitch.ui.TopBarViewEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform

object SearchViewEventTransitionMapper : (SearchViewEvent) -> Sequence<SearchTransition> {
    override fun invoke(viewEvent: SearchViewEvent): Sequence<SearchTransition> = when (viewEvent) {
        SearchBackClick -> sequenceOf(DisengageSearch)
        SearchEntered -> sequenceOf(EnterSearch)
        is SearchTextChanged -> sequenceOf(TextEntered(viewEvent.text))
    }
}

object TopBarViewSearchViewEventTransitionMapper : (TopBarViewEvent) -> Sequence<SearchTransition> {
    override fun invoke(viewEvent: TopBarViewEvent): Sequence<SearchTransition> = when (viewEvent) {
        SearchClick -> sequenceOf(EngageSearch)
        is TopBarSearchViewEvent -> SearchViewEventTransitionMapper(viewEvent.searchViewEvent)
        TopBarBackClick -> emptySequence()
    }
}

fun Flow<TopBarViewEvent>.asSearchState(
    coroutineScope: CoroutineScope,
    searchConfiguration: SearchConfiguration
): StateFlow<SearchState> {
    return transform {
        emitAll(TopBarViewSearchViewEventTransitionMapper(it).asFlow())
    }.mapToSearchState(searchConfiguration)
        .stateIn(coroutineScope, SharingStarted.Lazily, InactiveSearchState(searchConfiguration))
}

private fun Flow<SearchTransition>.mapToSearchState(searchConfiguration: SearchConfiguration) = scan<SearchTransition, SearchState>(
    InactiveSearchState(searchConfiguration)
) { state, transition -> SearchScanFunction(state, transition) }
