package com.yoxjames.openstitch.pattern.state

import com.yoxjames.openstitch.list.ListItemState
import com.yoxjames.openstitch.list.ListItemViewState
import com.yoxjames.openstitch.list.ListState
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.loading.asBoolean
import com.yoxjames.openstitch.pattern.ds.HotPatternsLoaded
import com.yoxjames.openstitch.pattern.ds.LoadingPatterns
import com.yoxjames.openstitch.pattern.ds.PatternListTransition
import com.yoxjames.openstitch.pattern.ds.PatternSearchLoaded
import com.yoxjames.openstitch.pattern.model.ListPattern
import com.yoxjames.openstitch.pattern.vs.PatternRowViewState
import com.yoxjames.openstitch.ui.generic.Divider
import com.yoxjames.openstitch.ui.generic.TitleRowState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.scan

data class PatternListState(
    val listPatterns: List<ListPattern> = emptyList(),
    val isHotPatterns: Boolean = true,
    val loadingState: LoadingState = LoadingState.LOADING
) {
    private val titleState = when (isHotPatterns && listPatterns.isNotEmpty()) {
        true -> sequenceOf(TitleRowState("\uD83d\uDD25 Hot Patterns"), Divider)
        false -> emptySequence()
    }
    val listState: ListState = ListState(
        (titleState + listPatterns.asSequence().map { PatternRow(listPattern = it, isLoading = loadingState.asBoolean) }).toList()
    )
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

fun Flow<PatternListTransition>.asState(): Flow<PatternListState> {
    return scan(PatternListState()) { listState, transition ->
        when (transition) {
            LoadingPatterns -> PatternListState(
                listPatterns = listState.listPatterns,
                isHotPatterns = listState.isHotPatterns,
                loadingState = LoadingState.LOADING
            )
            is HotPatternsLoaded -> PatternListState(
                listPatterns = transition.listPatterns,
                isHotPatterns = true,
                loadingState = LoadingState.COMPLETE
            )
            is PatternSearchLoaded -> PatternListState(
                listPatterns = transition.listPatterns,
                isHotPatterns = false,
                loadingState = LoadingState.COMPLETE
            )
        }
    }
}
