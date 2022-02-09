package com.yoxjames.openstitch.navigation

object NavigationStateFunction : (NavigationState, NavigationTransition) -> NavigationState {
    override fun invoke(state: NavigationState, transition: NavigationTransition): NavigationState = when (transition) {
        Back -> state.pop()
        is OpenPatternDetail -> state.push(PatternDetail(transition.patternId))
        OpenHotPatterns -> state.push(HotPatterns)
        is SearchPattern -> {
            if (state.navigationState is SearchingPatterns) {
                state.mod(SearchingPatterns(transition.searchText))
            } else {
                state.push(SearchingPatterns(transition.searchText))
            }
        }
    }
}
