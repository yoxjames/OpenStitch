package com.yoxjames.openstitch.pattern.vm

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.yoxjames.openstitch.list.Click
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.OpenPatternDetail
import com.yoxjames.openstitch.pattern.ds.LoadingPatterns
import com.yoxjames.openstitch.pattern.ds.PatternListDataSource
import com.yoxjames.openstitch.pattern.state.PatternListState
import com.yoxjames.openstitch.pattern.state.PatternRow
import com.yoxjames.openstitch.pattern.state.asState
import com.yoxjames.openstitch.search.EnteredSearchState
import com.yoxjames.openstitch.search.FocusedSearchState
import com.yoxjames.openstitch.search.InactiveSearchState
import com.yoxjames.openstitch.search.SearchConfiguration
import com.yoxjames.openstitch.search.SearchScanFunction
import com.yoxjames.openstitch.search.SearchState
import com.yoxjames.openstitch.search.SearchTransition
import com.yoxjames.openstitch.search.SearchViewStateMapper
import com.yoxjames.openstitch.search.TopBarViewSearchViewEventTransitionMapper
import com.yoxjames.openstitch.search.TypingSearchState
import com.yoxjames.openstitch.ui.DefaultTopBarViewState
import com.yoxjames.openstitch.ui.SearchTopBarViewState
import com.yoxjames.openstitch.ui.TopBarViewEvent
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

const val DEFAULT_HINT = "Search Patterns"

private val Flow<SearchTransition>.searchState get() = scan<SearchTransition, SearchState>(
    InactiveSearchState(SearchConfiguration(DEFAULT_HINT))
) { state, transition -> SearchScanFunction(state, transition) }

private fun SearchState.mapToTopBarViewState() = when (this) {
    is InactiveSearchState -> DefaultTopBarViewState(isSearchAvailable = true, isBackAvailable = false)
    is EnteredSearchState, is FocusedSearchState, is TypingSearchState -> SearchTopBarViewState(SearchViewStateMapper(this))
}

interface PatternListScreenDataSource {
    val state: Flow<PatternListState>
    val searchState: StateFlow<SearchState>
    val _topBarViewEvents: MutableSharedFlow<TopBarViewEvent>
}

class PatternListScreenDataSourceImpl(
    private val patternListDataSource: PatternListDataSource,
    private val coroutineScope: CoroutineScope,
) : PatternListScreenDataSource {
    override val state: Flow<PatternListState>
        get() = searchState.transform {
            if (it.text.isBlank()) {
                emit(LoadingPatterns)
                emitAll(patternListDataSource.loadHotPatterns())
            } else {
                emit(LoadingPatterns)
                emitAll(patternListDataSource.searchPatterns(it.text))
            }
        }.asState()
    override val searchState: StateFlow<SearchState>
        get() = _topBarViewEvents.transform {
            TopBarViewSearchViewEventTransitionMapper(it).forEach { emit(it) }
        }.searchState.stateIn(coroutineScope, SharingStarted.Lazily, InactiveSearchState(
            SearchConfiguration(DEFAULT_HINT)
        ))
    override val _topBarViewEvents: MutableSharedFlow<TopBarViewEvent> get() = MutableSharedFlow<TopBarViewEvent>()
}

@Composable
fun PatternListView(
    navigationTransitions: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>,
    patternListScreenDataSource: PatternListScreenDataSource,
) {
    val listState = rememberLazyListState()
    val state = patternListScreenDataSource.state.collectAsState(
        initial = PatternListState(
            listPatterns = emptyList(),
            isHotPatterns = true,
            loadingState = LoadingState.LOADING
        )
    ).value
    val searchState = patternListScreenDataSource.searchState.collectAsState()

    OpenStitchScaffold(
        onTopBarViewEvent = { patternListScreenDataSource._topBarViewEvents.emit(it) },
        topBarViewState = searchState.value.mapToTopBarViewState(),
        loadingViewState = state.loadingState.viewState
    ) {
        state.listState.viewState.Composable(
            scrollState = listState,
            viewEventListener = {
                val rowState = state.listState.items[it.pos]
                if (it.event is Click && rowState is PatternRow) {
                    navigationTransitions.emit(OpenPatternDetail(rowState.listPattern.id))
                }
            }
        )
    }
}
