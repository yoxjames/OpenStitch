package com.yoxjames.openstitch.pattern.vm

import androidx.compose.material.ExperimentalMaterialApi
import com.yoxjames.openstitch.filter.DefaultTagState
import com.yoxjames.openstitch.filter.TagState
import com.yoxjames.openstitch.filter.toggle
import com.yoxjames.openstitch.list.ChildViewEvent
import com.yoxjames.openstitch.list.Click
import com.yoxjames.openstitch.list.StatefulListItemViewEvent
import com.yoxjames.openstitch.loading.LoadState
import com.yoxjames.openstitch.loading.Loaded
import com.yoxjames.openstitch.loading.NotLoaded
import com.yoxjames.openstitch.loading.ViewScreen
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.OpenPatternDetail
import com.yoxjames.openstitch.navigation.PatternsScreen
import com.yoxjames.openstitch.pattern.ds.LoadingPatterns
import com.yoxjames.openstitch.pattern.ds.PatternListDataSource
import com.yoxjames.openstitch.pattern.ds.TagsChange
import com.yoxjames.openstitch.pattern.state.PatternListState
import com.yoxjames.openstitch.pattern.state.PatternRowItemState
import com.yoxjames.openstitch.pattern.state.asState
import com.yoxjames.openstitch.search.InactiveSearchState
import com.yoxjames.openstitch.search.SearchConfiguration
import com.yoxjames.openstitch.search.SearchState
import com.yoxjames.openstitch.search.asSearchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
    private val patternListViewEvents = MutableSharedFlow<PatternListScreenViewEvent>()

    private val topBarViewEvents = patternListViewEvents.filterIsInstance<PatternListTopBarViewEvent>()
        .map { it.event }
    private val listViewEvents = patternListViewEvents.filterIsInstance<PatternListViewEvent>()
        .map { it.event }

    private val tagState = listViewEvents
        .filterIsInstance<StatefulListItemViewEvent>()
        .map { it.state }
        .filterIsInstance<TagState>()
        .scan(DefaultTagState) { acc, value -> acc.toggle(value.tag) }

    private val searchState: StateFlow<SearchState> = topBarViewEvents.asSearchState(coroutineScope, searchConfiguration)

    private val patternListState get() = searchState.distinctUntilChangedBy { it.text }
        .combine(tagState) { searchState, tagsState -> Pair(searchState, tagsState) }
        .transform {
            emit(TagsChange(it.second))
            emit(LoadingPatterns)
            emitAll(patternListDataSource.loadPatterns(it.first, it.second))
        }.asState()

    private val patternListStateWithCacheing = views.map { it.navigationScreenState }
        .filterIsInstance<PatternsScreen>().scan<PatternsScreen, LoadState>(NotLoaded) { acc, it ->
            if (acc is NotLoaded || (acc is Loaded<*> && acc.loadTime + 1000 * 60 * 1 < System.currentTimeMillis())) {
                Loaded(loadTime = System.currentTimeMillis(), state = patternListState.shareIn(coroutineScope, SharingStarted.Lazily, replay = 1))
            } else {
                acc
            }
        }.filterIsInstance<Loaded<PatternListState>>()
        .distinctUntilChanged()
        .transformLatest { emitAll(it.state) }

    override val state: StateFlow<PatternListScreenState> = patternListStateWithCacheing.combine(searchState) { patternListState, searchState ->
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
        patternListViewEvents.emit(viewEvent)
    }

    override suspend fun start() {
        listViewEvents.collect {
            if (it.state is PatternRowItemState && it.viewEvent is Click) {
                navigationTransitions.emit(OpenPatternDetail(it.state.listPattern.id))
            }
        }
    }
}
