package com.yoxjames.openstitch.pattern.vm

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.loading.LoadingViewState
import com.yoxjames.openstitch.navigation.Back
import com.yoxjames.openstitch.navigation.NavigationScreenState
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.PatternDetail
import com.yoxjames.openstitch.pattern.ds.PatternDetailDataSource
import com.yoxjames.openstitch.pattern.state.LoadedPatternDetailState
import com.yoxjames.openstitch.pattern.state.LoadingPatternState
import com.yoxjames.openstitch.pattern.state.PatternDetailState
import com.yoxjames.openstitch.pattern.state.asState
import com.yoxjames.openstitch.pattern.vs.PatternDetailViewState
import com.yoxjames.openstitch.pattern.vs.mapper.PatternDetailViewStateMapper
import com.yoxjames.openstitch.ui.DefaultTopBarViewState
import com.yoxjames.openstitch.ui.DropDownItemViewState
import com.yoxjames.openstitch.ui.TopBarBackClick
import com.yoxjames.openstitch.ui.TopBarViewState
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
) {
    private val states: StateFlow<PatternDetailScreenState> = navigationScreenState.filterIsInstance<PatternDetail>()
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

    @Composable
    fun ComposeViewModel() {
        val state: PatternDetailScreenState = states.collectAsState().value
        val viewState = DetailScreenViewStateMapper(state)
        with(viewState) {
            OpenStitchScaffold(
                onTopBarViewEvent = { if (it is TopBarBackClick) navigationBus.emit(Back) },
                topBarViewState = topBarViewState,
                loadingViewState = loadingViewState,
            ) {
                when (val contentViewState = viewState.patternContentViewState) {
                    is LoadedPatternViewState -> contentViewState.patternDetailViewState.Composable(
                        viewEventListener = { }
                    )
                    LoadingPatternViewState -> Unit
                }
            }
        }
    }
}

object DetailScreenViewStateMapper : (PatternDetailScreenState) -> PatternDetailScreenViewState {
    override fun invoke(state: PatternDetailScreenState): PatternDetailScreenViewState {
        return PatternDetailScreenViewState(
            topBarViewState = DefaultTopBarViewState(
                isSearchAvailable = false,
                isBackAvailable = state.navigationState.isBackAvailable,
                dropDownMenu = listOf(
                    DropDownItemViewState(Icons.Default.Favorite, "Favorite")
                )
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

data class PatternDetailScreenState(
    val patternDetailState: PatternDetailState = LoadingPatternState,
    val loadingState: LoadingState = LoadingState.LOADING,
    val navigationState: NavigationState = NavigationState(emptyList())
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
