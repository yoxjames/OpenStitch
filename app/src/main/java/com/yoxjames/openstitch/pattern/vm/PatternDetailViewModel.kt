package com.yoxjames.openstitch.pattern.vm

import com.yoxjames.openstitch.DetailScreenState
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.pattern.asState
import com.yoxjames.openstitch.pattern.ds.PatternDetailDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PatternDetailViewModel @Inject constructor(
    private val patternDetailDataSource: PatternDetailDataSource,
    private val navigationStates: StateFlow<@JvmSuppressWildcards NavigationState>
) {
    fun contentState(patternId: Long): Flow<DetailScreenState> {
        return patternDetailDataSource.loadPattern(patternId).asState().map {
            DetailScreenState(
                contentState = it,
                loadingState = it.loadingState,
                navigationState = navigationStates.value
            )
        }
    }
}
