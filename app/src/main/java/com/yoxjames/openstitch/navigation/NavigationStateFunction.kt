package com.yoxjames.openstitch.navigation

object NavigationStateFunction : (NavigationState, NavigationTransition) -> NavigationState {
    override fun invoke(state: NavigationState, transition: NavigationTransition): NavigationState = when(transition) {
        Back -> state.pop()
        is OpenPatternDetail -> state.push(PatternDetail(transition.patternId))
        OpenPatterns -> state.push(PatternList)
    }
}