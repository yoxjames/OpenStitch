package com.yoxjames.openstitch.pattern.vm

import androidx.compose.material.ExperimentalMaterialApi
import com.yoxjames.openstitch.loading.LoadState
import com.yoxjames.openstitch.loading.Loaded
import com.yoxjames.openstitch.loading.NotLoaded
import com.yoxjames.openstitch.loading.ViewScreen
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.OpenPatternDetail
import com.yoxjames.openstitch.navigation.PatternsScreen
import com.yoxjames.openstitch.pattern.ds.LoadingPatterns
import com.yoxjames.openstitch.pattern.ds.PatternListDataSource
import com.yoxjames.openstitch.pattern.state.PatternListState
import com.yoxjames.openstitch.pattern.state.PatternRowItemState
import com.yoxjames.openstitch.pattern.state.asState
import com.yoxjames.openstitch.search.InactiveSearchState
import com.yoxjames.openstitch.search.SearchConfiguration
import com.yoxjames.openstitch.search.SearchScanFunction
import com.yoxjames.openstitch.search.SearchState
import com.yoxjames.openstitch.search.SearchTransition
import com.yoxjames.openstitch.search.TopBarViewSearchViewEventTransitionMapper
import com.yoxjames.openstitch.ui.TopBarViewEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

@ExperimentalMaterialApi
class PatternListViewModelImpl @Inject constructor(
    private val patternListDataSource: PatternListDataSource,
    private val coroutineScope: CoroutineScope,
    private val navigationTransitions: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>,
    private val views: Flow<@JvmSuppressWildcards ViewScreen>
) : PatternListViewModel {
    companion object {
        private val searchConfiguration = SearchConfiguration("Search Patterns")
    }
    private val topBarViewEvents: MutableSharedFlow<TopBarViewEvent> = MutableSharedFlow()

    private val searchState: StateFlow<SearchState> = topBarViewEvents.transform { topBarViewEvent ->
        TopBarViewSearchViewEventTransitionMapper(topBarViewEvent)
            .forEach { searchTransition -> emit(searchTransition) }
    }.mapToSearchState
        .stateIn(coroutineScope, SharingStarted.Lazily, InactiveSearchState(searchConfiguration))

    private val Flow<SearchTransition>.mapToSearchState get() = scan<SearchTransition, SearchState>(
        InactiveSearchState(searchConfiguration)
    ) { state, transition -> SearchScanFunction(state, transition) }

    private val _state get() = searchState.distinctUntilChangedBy { it.text }
        .transform {
            if (it.text.isBlank()) {
                emit(LoadingPatterns)
                emitAll(patternListDataSource.loadHotPatterns())
            } else {
                emit(LoadingPatterns)
                emitAll(patternListDataSource.searchPatterns(it.text))
            }
        }.asState()

    private val patternListState = views.map { it.navigationScreenState }
        .filterIsInstance<PatternsScreen>().scan<PatternsScreen, LoadState>(NotLoaded) { acc, it ->
            if (acc is NotLoaded || (acc is Loaded<*> && acc.loadTime + 1000 * 60 * 1 < System.currentTimeMillis())) {
                Loaded(loadTime = System.currentTimeMillis(), state = _state.shareIn(coroutineScope, SharingStarted.Lazily, replay = 1))
            } else {
                acc
            }
        }.filterIsInstance<Loaded<PatternListState>>()
        .distinctUntilChanged()
        .transformLatest { emitAll(it.state) }

    override val state: StateFlow<PatternListScreenState> = patternListState.combine(searchState) { patternListState, searchState ->
        PatternListScreenState(searchState, patternListState)
    }.stateIn(
        coroutineScope,
        SharingStarted.Lazily,
        PatternListScreenState(
            searchState = InactiveSearchState(searchConfiguration),
            patternListState = PatternListState.DEFAULT
        )
    )

    override suspend fun emitViewEvent(viewEvent: PatternListScreenViewEvent) {
        when (viewEvent) {
            is PatternListTopBarViewEvent -> topBarViewEvents.emit(viewEvent.event)
            is PatternListViewEvent -> when (val rowState = viewEvent.event.state) {
                is PatternRowItemState -> navigationTransitions.emit(OpenPatternDetail(rowState.listPattern.id))
            }
        }
    }
}
