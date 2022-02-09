package com.yoxjames.openstitch.navigation

/**
 * NavigationScreenState is just an object that represents where we are in the app. Essentially
 * what screen is showing.
 */
sealed interface NavigationScreenState

/**
 * Nothing is showing. This is the default state and means the app has not initialized yet.
 * There should never be a circumstance where you can navigate to this state.
 */
object None : NavigationScreenState

/**
 * Hot patterns showing.
 */
object HotPatterns : NavigationScreenState, PatternsScreen {
    override val searchText: String = ""
}

/**
 * Searching for a pattern.
 */
data class SearchingPatterns(
    override val searchText: String
) : NavigationScreenState, PatternsScreen

/**
 * Pattern detail
 */
data class PatternDetail(
    val patternId: Long,
) : NavigationScreenState

sealed interface PatternsScreen : NavigationScreenState {
    val searchText: String
}

/**
 * NavigationState is a Stack of NavigationScreenStates. The idea of the stack is it allows
 * us to transition to new NavigationScreenStates when the Back button is pushed. This
 * is just a way for us to hold a backstack and the user navigates through the app.
 */
@JvmInline
value class NavigationState(
    private val navigationStack: List<NavigationScreenState>
) {
    val navigationState get() = navigationStack.lastOrNull() ?: None
    fun push(navigationScreenState: NavigationScreenState) = NavigationState(navigationStack + navigationScreenState)
    fun pop() = NavigationState(navigationStack.dropLast(1))
    fun mod(navigationScreenState: NavigationScreenState): NavigationState {
        return pop().push(navigationScreenState)
    }
    val isBackAvailable: Boolean get() = navigationStack.size > 1
}
