package com.yoxjames.openstitch.search

sealed interface SearchTransition

object EngageSearch : SearchTransition

object DisengageSearch : SearchTransition

data class TextEntered(
    val text: String
) : SearchTransition

object EnterSearch : SearchTransition

object SearchScanFunction : (SearchState, SearchTransition) -> SearchState {
    override fun invoke(state: SearchState, transition: SearchTransition): SearchState = when(state) {
        is InactiveSearchState -> transitionInactiveState(state, transition)
        is FocusedSearchState -> transitionFocusedSearchState(state, transition)
        is TypingSearchState -> transitionTypingSearchState(state, transition)
        is EnteredSearchState -> transitionEnteredSearchState(state, transition)
    }

    private fun transitionInactiveState(state: InactiveSearchState, transition: SearchTransition) = when(transition) {
        is DisengageSearch -> state
        is EngageSearch -> FocusedSearchState(searchConfiguration = state.searchConfiguration)
        is TextEntered -> TypingSearchState(text = transition.text, searchConfiguration = state.searchConfiguration)
        is EnterSearch -> state // This might be an error state?
    }

    private fun transitionFocusedSearchState(state: FocusedSearchState, transition: SearchTransition) = when(transition) {
        is DisengageSearch -> InactiveSearchState(searchConfiguration = state.searchConfiguration)
        is EngageSearch -> state
        is TextEntered -> TypingSearchState(text = transition.text, searchConfiguration = state.searchConfiguration)
        is EnterSearch -> state // Do nothing
    }

    private fun transitionTypingSearchState(state: TypingSearchState, transition: SearchTransition) = when(transition) {
        is DisengageSearch -> InactiveSearchState(searchConfiguration = state.searchConfiguration)
        is EngageSearch -> state
        is TextEntered -> TypingSearchState(text = transition.text, searchConfiguration = state.searchConfiguration)
        is EnterSearch -> EnteredSearchState(searchText = state.text, searchConfiguration = state.searchConfiguration)
    }

    private fun transitionEnteredSearchState(state: EnteredSearchState, transition: SearchTransition) = when(transition) {
        is DisengageSearch -> InactiveSearchState(searchConfiguration = state.searchConfiguration)
        is EngageSearch -> state
        is TextEntered -> TypingSearchState(text = transition.text, searchConfiguration = state.searchConfiguration)
        is EnterSearch -> state
    }
}