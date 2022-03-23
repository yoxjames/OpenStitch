package com.yoxjames.openstitch.pattern.vm.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoxjames.openstitch.core.OpenStitchViewModel
import com.yoxjames.openstitch.navigation.HotPatterns
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.PatternDetail
import com.yoxjames.openstitch.pattern.ds.PatternDetailDataSource
import com.yoxjames.openstitch.pattern.state.asState
import com.yoxjames.openstitch.pattern.vm.destinations.PatternDetailViewDestination
import com.yoxjames.openstitch.ui.ActionClick
import com.yoxjames.openstitch.ui.SearchClick
import com.yoxjames.openstitch.ui.TopBarBackClick
import com.yoxjames.openstitch.ui.TopBarSearchViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PatternDetailViewModel @Inject constructor(
    private val patternDetailDataSource: PatternDetailDataSource,
    private val savedStateHandle: SavedStateHandle,
) : OpenStitchViewModel<PatternDetailScreenState, PatternDetailViewEvent>, ViewModel() {
    private val patternId get() = PatternDetailViewDestination.argsFrom(savedStateHandle).patternId
    private val patternDetailViewEvents = MutableSharedFlow<PatternDetailViewEvent>()

    override val state: StateFlow<PatternDetailScreenState> = patternDetailDataSource.loadPattern(patternId = patternId)
        .asState()
        .map {
            PatternDetailScreenState(
                patternDetailState = it,
                loadingState = it.loadingState,
                // TODO CK - Refactor this to simply ask whether to show up enabled and get rid of NavigationState
                navigationState = NavigationState(listOf(HotPatterns, PatternDetail(patternId)))
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, PatternDetailScreenState())

    override suspend fun emitViewEvent(viewEvent: PatternDetailViewEvent) = when (viewEvent) {
        is PatternDetailTopBarViewEvent -> patternDetailViewEvents.emit(viewEvent)
    }

    override suspend fun start() {
        patternDetailViewEvents.collect {
            when (it) {
                is PatternDetailTopBarViewEvent -> when (it.topBarViewEvent) {
                    is ActionClick,
                    is TopBarSearchViewEvent,
                    SearchClick,
                    TopBarBackClick -> Unit
                }
            }
        }
    }
}
