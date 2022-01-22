package com.yoxjames.openstitch.search

data class SearchConfiguration(
    val hint: String
)

sealed interface SearchState {
    val searchConfiguration: SearchConfiguration
}

data class InactiveSearchState(
    override val searchConfiguration: SearchConfiguration
) : SearchState

data class FocusedSearchState(
    override val searchConfiguration: SearchConfiguration
) : SearchState

data class TypingSearchState(
    val text: String,
    override val searchConfiguration: SearchConfiguration
) : SearchState

data class EnteredSearchState(
    val searchText: String,
    override val searchConfiguration: SearchConfiguration
) : SearchState, ActiveSearchState

interface ActiveSearchState