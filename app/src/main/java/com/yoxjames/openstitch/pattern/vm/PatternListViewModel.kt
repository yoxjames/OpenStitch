package com.yoxjames.openstitch.pattern.vm

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.yoxjames.openstitch.list.Click
import com.yoxjames.openstitch.list.StatefulListViewEvent
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

class PatternListViewModel @Inject constructor(
    private val patternListDataSource: PatternListDataSource,
    private val coroutineScope: CoroutineScope,
    private val navigationTransitions: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>
) {
    companion object {
        private val searchConfiguration = SearchConfiguration("Search Patterns")
    }
    private val _topBarViewEvents = MutableSharedFlow<TopBarViewEvent>()
    private val _listViewEvents = MutableSharedFlow<StatefulListViewEvent>()

    private val searchState = _topBarViewEvents.transform {
        TopBarViewSearchViewEventTransitionMapper(it).forEach { emit(it) }
    }.searchState.stateIn(coroutineScope, SharingStarted.Lazily, InactiveSearchState(searchConfiguration))

    val Flow<SearchTransition>.searchState get() = scan<SearchTransition, SearchState>(
        InactiveSearchState(searchConfiguration)
    ) { state, transition -> SearchScanFunction(state, transition) }

    val state = searchState.transform {
        if (it.text.isBlank()) {
            emit(LoadingPatterns)
            emitAll(patternListDataSource.loadHotPatterns())
        } else {
            emit(LoadingPatterns)
            emitAll(patternListDataSource.searchPatterns(it.text))
        }
    }.asState()

    @Composable
    fun ComposeViewModel() {
        val listState = rememberLazyListState()
        val state = this.state.collectAsState(
            initial = PatternListState(
                listPatterns = emptyList(),
                isHotPatterns = true,
                loadingState = LoadingState.LOADING
            )
        ).value
        val searchState = searchState.collectAsState()

        OpenStitchScaffold(
            onTopBarViewEvent = { _topBarViewEvents.emit(it) },
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

    private fun SearchState.mapToTopBarViewState() = when (this) {
        is InactiveSearchState -> DefaultTopBarViewState(isSearchAvailable = true, isBackAvailable = false)
        is EnteredSearchState, is FocusedSearchState, is TypingSearchState -> SearchTopBarViewState(SearchViewStateMapper(this))
    }
}
