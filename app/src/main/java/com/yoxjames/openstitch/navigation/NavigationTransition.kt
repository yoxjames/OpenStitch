package com.yoxjames.openstitch.navigation

sealed interface NavigationTransition

object OpenPatterns : NavigationTransition

object Back : NavigationTransition

data class OpenPatternDetail(
    val patternId: Long
) : NavigationTransition