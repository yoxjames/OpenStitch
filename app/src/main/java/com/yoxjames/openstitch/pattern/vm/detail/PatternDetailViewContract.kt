package com.yoxjames.openstitch.pattern.vm.detail

import com.yoxjames.openstitch.core.ViewEvent
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.loading.LoadingViewState
import com.yoxjames.openstitch.pattern.state.LoadedPatternDetailState
import com.yoxjames.openstitch.pattern.state.LoadingPatternState
import com.yoxjames.openstitch.pattern.state.PatternDetailState
import com.yoxjames.openstitch.pattern.vs.PatternDetailViewState
import com.yoxjames.openstitch.pattern.vs.mapper.PatternDetailViewStateMapper
import com.yoxjames.openstitch.ui.DefaultTopBarViewState
import com.yoxjames.openstitch.ui.TopBarViewEvent
import com.yoxjames.openstitch.ui.TopBarViewState

object DetailScreenViewStateMapper : (PatternDetailScreenState) -> PatternDetailScreenViewState {
    override fun invoke(state: PatternDetailScreenState): PatternDetailScreenViewState {
        return PatternDetailScreenViewState(
            topBarViewState = DefaultTopBarViewState(
                isSearchAvailable = false,
                isBackAvailable = true
            ),
            patternContentViewState = when (state.patternDetailState) {
                is LoadedPatternDetailState -> LoadedPatternViewState(
                    PatternDetailViewStateMapper(state.patternDetailState)
                )
                LoadingPatternState -> LoadingPatternViewState
            },
            loadingViewState = state.loadingState.viewState
        )
    }
}

sealed interface PatternDetailViewEvent : ViewEvent

@JvmInline value class PatternDetailTopBarViewEvent(
    val topBarViewEvent: TopBarViewEvent
) : PatternDetailViewEvent

data class PatternDetailScreenState(
    val patternDetailState: PatternDetailState = LoadingPatternState,
    val loadingState: LoadingState = LoadingState.LOADING,
)

sealed interface PatternContentViewState

object LoadingPatternViewState : PatternContentViewState

@JvmInline value class LoadedPatternViewState(
    val patternDetailViewState: PatternDetailViewState
) : PatternContentViewState

data class PatternDetailScreenViewState(
    val topBarViewState: TopBarViewState,
    val patternContentViewState: PatternContentViewState,
    val loadingViewState: LoadingViewState
)
