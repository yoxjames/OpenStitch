package com.yoxjames.openstitch.pattern.vm.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoxjames.openstitch.core.OpenStitchViewModel
import com.yoxjames.openstitch.navigation.HotPatterns
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.PatternDetail
import com.yoxjames.openstitch.pattern.ds.PatternDetailDataSource
import com.yoxjames.openstitch.pattern.state.asState
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
) : OpenStitchViewModel<PatternDetailScreenState, PatternDetailViewEvent>, ViewModel() {
    private val patternDetailViewEvents = MutableSharedFlow<PatternDetailViewEvent>()

    override val state: StateFlow<PatternDetailScreenState> = patternDetailDataSource.loadPattern(patternId = 1234).asState()
        .map {
            PatternDetailScreenState(
                    patternDetailState = it,
                    loadingState = it.loadingState,
                    navigationState = NavigationState(listOf(HotPatterns, PatternDetail(1234)))
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
                    SearchClick -> Unit
                    TopBarBackClick -> TODO()
                }
            }
        }
    }
}
