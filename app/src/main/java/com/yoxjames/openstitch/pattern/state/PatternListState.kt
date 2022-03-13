package com.yoxjames.openstitch.pattern.state

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.list.ListItemState
import com.yoxjames.openstitch.list.ListItemViewEvent
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

@ExperimentalMaterialApi
data class PatternListState(
    val listPatterns: List<ListPattern> = emptyList(),
    val isHotPatterns: Boolean = true,
    val loadingState: LoadingState = LoadingState.LOADING,
    val showKeyboard: Boolean = false,
) {
    companion object {
        val DEFAULT = PatternListState()
    }
    private val titleState = when (isHotPatterns && listPatterns.isNotEmpty()) {
        true -> sequenceOf(TitleRowState("\uD83d\uDD25 Hot Patterns"), Divider)
        false -> emptySequence()
    }
    val listState: ListState = ListState(
        (titleState + listPatterns.asSequence().map { PatternRowItemState(listPattern = it, isLoading = loadingState.asBoolean) }).toList()
    )
}
@ExperimentalMaterialApi
data class PatternRowItemState(
    val listPattern: ListPattern,
    val isLoading: Boolean
) : ListItemState {
    @Composable
    override fun RowView(onViewEvent: ViewEventListener<ListItemViewEvent>) {
        viewState.Composable(viewEventListener = onViewEvent)
    }
    private val viewState = PatternRowViewState(
        name = listPattern.name,
        author = listPattern.author,
        imageUrl = listPattern.thumbnail,
        isLoading = isLoading
    )
}

@OptIn(ExperimentalMaterialApi::class)
fun Flow<PatternListTransition>.asState(): Flow<PatternListState> {
    return scan(PatternListState.DEFAULT) { listState, transition ->
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
