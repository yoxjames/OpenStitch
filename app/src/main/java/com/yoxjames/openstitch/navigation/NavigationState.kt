package com.yoxjames.openstitch.navigation

sealed interface NavigationScreenState

object None : NavigationScreenState

object PatternList : NavigationScreenState

data class PatternDetail(
    val patternId: Int,
) : NavigationScreenState

@JvmInline
value class NavigationState(
    val navigationStack: List<NavigationScreenState>
) {
    val navigationState get() = navigationStack.lastOrNull() ?: None
    fun push(navigationScreenState: NavigationScreenState) = NavigationState(navigationStack + navigationScreenState)
    fun pop() = NavigationState(navigationStack.dropLast(1))
}