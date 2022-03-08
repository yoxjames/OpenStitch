package com.yoxjames.openstitch.pattern.vm

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.yoxjames.openstitch.list.Click
import com.yoxjames.openstitch.loading.LoadState
import com.yoxjames.openstitch.loading.Loaded
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.loading.NotLoaded
import com.yoxjames.openstitch.loading.ViewScreen
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.OpenPatternDetail
import com.yoxjames.openstitch.navigation.PatternsScreen
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
import com.yoxjames.openstitch.ui.SearchBackClick
import com.yoxjames.openstitch.ui.SearchClick
import com.yoxjames.openstitch.ui.SearchEntered
import com.yoxjames.openstitch.ui.SearchTextChanged
import com.yoxjames.openstitch.ui.SearchTopBarViewState
import com.yoxjames.openstitch.ui.TopBarBackClick
import com.yoxjames.openstitch.ui.TopBarSearchViewEvent
import com.yoxjames.openstitch.ui.TopBarViewEvent
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold
import com.yoxjames.openstitch.ui.generic.Keyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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
import kotlinx.coroutines.launch

private val searchConfiguration = SearchConfiguration("Search Patterns")

private fun SearchState.mapToTopBarViewState() = when (this) {
    is InactiveSearchState -> DefaultTopBarViewState(isSearchAvailable = true, isBackAvailable = false)
    is EnteredSearchState, is FocusedSearchState, is TypingSearchState -> SearchTopBarViewState(SearchViewStateMapper(this))
}

interface PatternListViewModel {
    val searchState: StateFlow<SearchState>
    val state: StateFlow<PatternListState>
    val navigationTransitions: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>
    val _topBarViewEvents: MutableSharedFlow<TopBarViewEvent>
}

class PatternListViewModelImpl(
    private val patternListDataSource: PatternListDataSource,
    private val coroutineScope: CoroutineScope,
    override val navigationTransitions: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>,
    private val views: Flow<@JvmSuppressWildcards ViewScreen>,
) : PatternListViewModel {
    companion object {
        private const val CACHE_TIME_MILLIS = 1000 * 60 * 1
    }
    private val Flow<SearchTransition>.searchState get() = scan<SearchTransition, SearchState>(
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

    override val _topBarViewEvents: MutableSharedFlow<TopBarViewEvent> = MutableSharedFlow()

    override val searchState: StateFlow<SearchState>
        get() = _topBarViewEvents.transform { TopBarViewSearchViewEventTransitionMapper(it).forEach { emit(it) } }
            .searchState
            .stateIn(coroutineScope, SharingStarted.Lazily, InactiveSearchState(searchConfiguration))

    override val state: StateFlow<PatternListState>
        get() = views.map { it.navigationScreenState }
            .filterIsInstance<PatternsScreen>()
            .scan<PatternsScreen, LoadState>(NotLoaded) { acc, _ ->
                if (acc is NotLoaded || (acc is Loaded<*> && acc.loadTime + CACHE_TIME_MILLIS < System.currentTimeMillis())) {
                    Loaded(loadTime = System.currentTimeMillis(), state = acc)
                } else {
                    acc
                }
            }
            .filterIsInstance<Loaded<PatternListState>>()
            .map { it.state }
            .stateIn(coroutineScope, SharingStarted.Lazily, initialValue = PatternListState.DEFAULT)
}

@Composable
fun PatternListView(listState: LazyListState, patternListViewModel: PatternListViewModel) {
    val state = patternListViewModel.state.collectAsState(initial = PatternListState.DEFAULT).value
    val searchState = patternListViewModel.searchState.collectAsState()

    Keyboard(show = state.showKeyboard)

    OpenStitchScaffold(
        onTopBarViewEvent = { patternListViewModel._topBarViewEvents.emit(it) },
        topBarViewState = searchState.value.mapToTopBarViewState(),
        loadingViewState = state.loadingState.viewState
    ) {
        state.listState.viewState.Composable(
            scrollState = listState,
            viewEventListener = {
                val rowState = state.listState.items[it.pos]
                if (it.event is Click && rowState is PatternRow) {
                    patternListViewModel.navigationTransitions.emit(OpenPatternDetail(rowState.listPattern.id))
                }
            }
        )
    }
}

