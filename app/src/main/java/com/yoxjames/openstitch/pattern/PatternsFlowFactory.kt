package com.yoxjames.openstitch.pattern

import com.yoxjames.openstitch.search.ActiveSearchState
import com.yoxjames.openstitch.search.EnteredSearchState
import com.yoxjames.openstitch.search.FocusedSearchState
import com.yoxjames.openstitch.search.InactiveSearchState
import com.yoxjames.openstitch.search.SearchState
import com.yoxjames.openstitch.search.TypingSearchState
import com.yoxjames.openstitch.list.ListItemState
import com.yoxjames.openstitch.list.ListItemViewState
import com.yoxjames.openstitch.list.ListState
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.loading.asBoolean
import com.yoxjames.openstitch.ui.generic.Divider
import com.yoxjames.openstitch.ui.generic.TitleRowState
import com.yoxjames.openstitch.ui.pattern.PatternRowViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@FlowPreview
class PatternsFlowFactory @Inject constructor(
    private val patternsService: PatternsService,
    private val searchState: Flow<@JvmSuppressWildcards SearchState>,
) {
    val flow: Flow<PatternsState> = searchState.distinctUntilChangedBy { it is ActiveSearchState }
        .filter { it is EnteredSearchState || it is InactiveSearchState }
        .transform {
                when (it) {
                    is InactiveSearchState, is FocusedSearchState, is TypingSearchState -> {
                        emitAll(patternsService.loadHotPatterns())
                    }
                    is EnteredSearchState -> {
                        if (it.searchText.isBlank()) {
                            emitAll(patternsService.loadHotPatterns())
                        } else {
                            emitAll(patternsService.searchPatterns(it.searchText))
                        }
                    }
                }
            }
        .scan(
            PatternsState(listPatterns = emptyList(), isHotPatterns = true, loadingState = LoadingState.LOADING)
        ) { listState, transition ->
            when (transition) {
                LoadingPatterns -> PatternsState(
                    listPatterns = listState.listPatterns,
                    isHotPatterns = listState.isHotPatterns,
                    loadingState = LoadingState.LOADING
                )
                is HotPatternsLoaded -> PatternsState(
                    listPatterns = transition.listPatterns,
                    isHotPatterns = true,
                    loadingState = LoadingState.COMPLETE
                )
                is PatternSearchLoaded -> PatternsState(
                    listPatterns = transition.listPatterns,
                    isHotPatterns = false,
                    loadingState = LoadingState.COMPLETE
                )
            }
        }
}

data class PatternRow(
    val listPattern: ListPattern,
    val isLoading: Boolean
) : ListItemState {
    override val viewState: ListItemViewState = PatternRowViewState(
        name = listPattern.name,
        author = listPattern.author,
        imageUrl = listPattern.thumbnail,
        isLoading = isLoading
    )
}

data class PatternsState(
    val listPatterns: List<ListPattern>,
    val isHotPatterns: Boolean,
    val loadingState: LoadingState
) {
    private val titleState = when (isHotPatterns && listPatterns.isNotEmpty()) {
        true -> sequenceOf(TitleRowState("\uD83d\uDD25 Hot Patterns"), Divider)
        false -> emptySequence()
    }
    val listState: ListState = ListState(
        (titleState + listPatterns.asSequence().map { PatternRow(listPattern = it, isLoading = loadingState.asBoolean) }).toList()
    )
}