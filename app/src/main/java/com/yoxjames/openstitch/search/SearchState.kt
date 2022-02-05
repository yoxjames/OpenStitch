package com.yoxjames.openstitch.search

data class SearchConfiguration(
    val hint: String
)

sealed interface SearchState {
    val searchConfiguration: SearchConfiguration
    val text: String
}

data class InactiveSearchState(
    override val searchConfiguration: SearchConfiguration,
) : SearchState {
    override val text: String = ""
}

data class FocusedSearchState(
    override val searchConfiguration: SearchConfiguration,
) : SearchState {
    override val text: String = ""
}

data class TypingSearchState(
    override val text: String,
    override val searchConfiguration: SearchConfiguration
) : SearchState

data class EnteredSearchState(
    val searchText: String,
    override val searchConfiguration: SearchConfiguration
) : SearchState, ActiveSearchState {
    override val text: String = searchText
}

interface ActiveSearchState