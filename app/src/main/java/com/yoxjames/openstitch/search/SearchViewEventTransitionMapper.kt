package com.yoxjames.openstitch.search

import com.yoxjames.openstitch.ui.SearchBackClick
import com.yoxjames.openstitch.ui.SearchClick
import com.yoxjames.openstitch.ui.SearchEntered
import com.yoxjames.openstitch.ui.SearchTextChanged
import com.yoxjames.openstitch.ui.SearchViewEvent
import com.yoxjames.openstitch.ui.TopBarBackClick
import com.yoxjames.openstitch.ui.TopBarSearchViewEvent
import com.yoxjames.openstitch.ui.TopBarViewEvent

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
