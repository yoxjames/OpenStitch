package com.yoxjames.openstitch.navigation

/**
 * Events that cause changes in Navigation. The simplest example would be Back which would
 * be caused by hitting the back button on Android. However, this concept is not
 * directly tied to Android and represents the user's Intent to navigate somewhere else.
 * Thus, while these might be mapped from ViewEvents they are not ViewEvents.
 */
sealed interface NavigationTransition

/**
 * Open the pattern list
 */
object OpenHotPatterns : NavigationTransition

/**
 * Intent to search for a pattern.
 */
data class SearchPattern(
    val searchText: String
) : NavigationTransition

/**
 * Intent to navigate up the back stack.
 */
object Back : NavigationTransition

/**
 * Open the detail view for a particular patternId.
 */
data class OpenPatternDetail(
    val patternId: Long
) : NavigationTransition
