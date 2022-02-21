package com.yoxjames.openstitch.search

import com.yoxjames.openstitch.ui.SearchViewState

object SearchViewStateMapper : (SearchState) -> SearchViewState {
    override fun invoke(state: SearchState): SearchViewState = SearchViewState(state.searchConfiguration.hint, state.text)
}
