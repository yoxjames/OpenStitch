package com.yoxjames.openstitch.pattern.vm.list

import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoxjames.openstitch.core.OpenStitchViewModel
import com.yoxjames.openstitch.filter.DefaultTagState
import com.yoxjames.openstitch.filter.TagState
import com.yoxjames.openstitch.filter.toggle
import com.yoxjames.openstitch.list.Click
import com.yoxjames.openstitch.list.StatefulListItemViewEvent
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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform

@ExperimentalMaterialApi
@HiltViewModel
class PatternListViewModel @Inject constructor(
    private val patternListDataSource: PatternListDataSource,
) : OpenStitchViewModel<PatternListScreenState, PatternListScreenViewEvent>, ViewModel() {
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

    private val searchState: StateFlow<SearchState> = topBarViewEvents.asSearchState(viewModelScope, searchConfiguration)

    private val patternListState get() = searchState.distinctUntilChangedBy { it.text }
        .combine(tagState) { searchState, tagsState -> Pair(searchState, tagsState) }
        .transform {
            emit(TagsChange(it.second))
            emit(LoadingPatterns)
            emitAll(patternListDataSource.loadPatterns(it.first, it.second))
        }.asState()

//    private val patternListStateWithCacheing = views.map { it.navigationScreenState }
//        .filterIsInstance<PatternsScreen>().scan<PatternsScreen, LoadState>(NotLoaded) { acc, it ->
//            if (acc is NotLoaded || (acc is Loaded<*> && acc.loadTime + 1000 * 60 * 1 < System.currentTimeMillis())) {
//                Loaded(loadTime = System.currentTimeMillis(), state = patternListState.shareIn(viewModelScope, SharingStarted.Lazily, replay = 1))
//            } else {
//                acc
//            }
//        }.filterIsInstance<Loaded<PatternListState>>()
//        .distinctUntilChanged()
//        .transformLatest { emitAll(it.state) }

    override val state: StateFlow<PatternListScreenState> = patternListState.combine(searchState) { patternListState, searchState ->
        PatternListScreenState(searchState, patternListState)
    }.stateIn(
        viewModelScope,
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
//                navigationTransitions.emit(OpenPatternDetail(it.state.listPattern.id))
                TODO()
            }
        }
    }
}
