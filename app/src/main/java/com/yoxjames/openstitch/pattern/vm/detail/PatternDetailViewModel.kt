package com.yoxjames.openstitch.pattern.vm.detail

import com.google.accompanist.pager.ExperimentalPagerApi
import com.yoxjames.openstitch.core.OpenStitchViewModel
import com.yoxjames.openstitch.navigation.Back
import com.yoxjames.openstitch.navigation.NavigationScreenState
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.PatternDetail
import com.yoxjames.openstitch.pattern.ds.PatternDetailDataSource
import com.yoxjames.openstitch.pattern.state.asState
import com.yoxjames.openstitch.ui.SearchClick
import com.yoxjames.openstitch.ui.TopBarBackClick
import com.yoxjames.openstitch.ui.TopBarSearchViewEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@ExperimentalPagerApi
class PatternDetailViewModel @Inject constructor(
    private val patternDetailDataSource: PatternDetailDataSource,
    private val navigationStates: StateFlow<@JvmSuppressWildcards NavigationState>,
    private val navigationScreenState: StateFlow<@JvmSuppressWildcards NavigationScreenState>,
    private val navigationBus: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>,
    private val coroutineScope: CoroutineScope
) : OpenStitchViewModel<PatternDetailScreenState, PatternDetailViewEvent> {
    private val patternDetailViewEvents = MutableSharedFlow<PatternDetailViewEvent>()

    override val state: StateFlow<PatternDetailScreenState> = navigationScreenState.filterIsInstance<PatternDetail>()
        .transform {
            emitAll(
                patternDetailDataSource.loadPattern(patternId = it.patternId).asState()
                    .map {
                        PatternDetailScreenState(
                            patternDetailState = it,
                            loadingState = it.loadingState,
                            navigationState = navigationStates.value
                        )
                    }
            )
        }.stateIn(coroutineScope, SharingStarted.Lazily, PatternDetailScreenState())

    override suspend fun emitViewEvent(viewEvent: PatternDetailViewEvent) = when (viewEvent) {
        is PatternDetailTopBarViewEvent -> patternDetailViewEvents.emit(viewEvent)
    }

    override suspend fun start() {
        patternDetailViewEvents.collect {
            when (it) {
                is PatternDetailTopBarViewEvent -> when (it.topBarViewEvent) {
                    is TopBarSearchViewEvent,
                    SearchClick -> Unit
                    TopBarBackClick -> navigationBus.emit(Back)
                }
            }
        }
    }
}
