package com.yoxjames.openstitch.navigation

sealed interface NavigationScreenState

object None : NavigationScreenState

object PatternList : NavigationScreenState

data class PatternDetail(
    val patternId: Long,
) : NavigationScreenState

@JvmInline
value class NavigationState(
    val navigationStack: List<NavigationScreenState>
) {
    val navigationState get() = navigationStack.lastOrNull() ?: None
    fun push(navigationScreenState: NavigationScreenState) = NavigationState(navigationStack + navigationScreenState)
    fun pop() = NavigationState(navigationStack.dropLast(1))
    val isBackAvailable: Boolean get() = navigationStack.size > 1
}