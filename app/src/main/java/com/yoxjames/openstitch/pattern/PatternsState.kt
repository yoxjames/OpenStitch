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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.scan
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class HotPatternFlowFactory @Inject constructor(
    private val patternsService: PatternsService,
    private val searchState: Flow<@JvmSuppressWildcards SearchState>,
) {
    val flow: Flow<PatternsState> = searchState.distinctUntilChangedBy { it is ActiveSearchState }
        .filter { (it is EnteredSearchState && it.searchText.isNotBlank()) || it is InactiveSearchState }
        .flatMapConcat {
                when (it) {
                    is InactiveSearchState, is FocusedSearchState, is TypingSearchState -> {
                        patternsService.loadHotPatterns()
                    }
                    is EnteredSearchState -> {
                        patternsService.searchPatterns(it.searchText)
                    }
                }
            }
        .scan(
            PatternsState(patterns = emptyList(), isHotPatterns = true, loadingState = LoadingState.LOADING)
        ) { listState, transition ->
            when (transition) {
                LoadingPatterns -> PatternsState(
                    patterns = listState.patterns,
                    isHotPatterns = listState.isHotPatterns,
                    loadingState = LoadingState.LOADING
                )
                is HotPatternsLoaded -> PatternsState(
                    patterns = transition.patterns,
                    isHotPatterns = true,
                    loadingState = LoadingState.COMPLETE
                )
                is PatternSearchLoaded -> PatternsState(
                    patterns = transition.patterns,
                    isHotPatterns = false,
                    loadingState = LoadingState.COMPLETE
                )
            }
        }
}

data class PatternRow(
    val pattern: Pattern,
    val isLoading: Boolean
) : ListItemState {
    override val viewState: ListItemViewState = PatternRowViewState(
        name = pattern.name,
        author = pattern.author,
        imageUrl = pattern.imageUrl,
        isLoading = isLoading
    )
}

data class PatternsState(
    val patterns: List<Pattern>,
    val isHotPatterns: Boolean,
    val loadingState: LoadingState
) {
    private val titleState = when (isHotPatterns && patterns.isNotEmpty()) {
        true -> sequenceOf(TitleRowState("\uD83d\uDD25 Hot Patterns"), Divider)
        false -> emptySequence()
    }
    val listState: ListState = ListState(
        (titleState + patterns.asSequence().map { PatternRow(pattern = it, isLoading = loadingState.asBoolean) }).toList()
    )
}