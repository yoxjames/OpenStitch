package com.yoxjames.openstitch.pattern.state

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.filter.DefaultTagState
import com.yoxjames.openstitch.filter.TagsState
import com.yoxjames.openstitch.filter.asListState
import com.yoxjames.openstitch.list.ListItemState
import com.yoxjames.openstitch.list.ListItemViewEvent
import com.yoxjames.openstitch.list.ListState
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.loading.asBoolean
import com.yoxjames.openstitch.pattern.ds.LoadingPatterns
import com.yoxjames.openstitch.pattern.ds.PatternListTransition
import com.yoxjames.openstitch.pattern.ds.PatternsLoaded
import com.yoxjames.openstitch.pattern.ds.TagsChange
import com.yoxjames.openstitch.pattern.model.ListPattern
import com.yoxjames.openstitch.pattern.vs.PatternRowViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.scan

@ExperimentalMaterialApi
data class PatternListState(
    val listPatterns: List<ListPattern> = emptyList(),
    val tagsState: TagsState = DefaultTagState,
    val isHotPatterns: Boolean = true,
    val loadingState: LoadingState = LoadingState.LOADING,
    val showKeyboard: Boolean = false,
) {
    companion object {
        val DEFAULT = PatternListState()
    }

    val chipList = tagsState.asListState()
    val listState: ListState = ListState(
        listPatterns.map { PatternRowItemState(listPattern = it, isLoading = loadingState.asBoolean) }
    )
}
@ExperimentalMaterialApi
data class PatternRowItemState(
    val listPattern: ListPattern,
    val isLoading: Boolean
) : ListItemState {
    @Composable
    override fun ItemView(onViewEvent: ViewEventListener<ListItemViewEvent>) {
        viewState.ItemContent(viewEventListener = onViewEvent)
    }
    private val viewState = PatternRowViewState(
        name = listPattern.name,
        author = listPattern.author,
        imageUrl = listPattern.thumbnail,
        isLoading = isLoading
    )
}

fun Flow<PatternListTransition>.asState(): Flow<PatternListState> {
    return scan(PatternListState.DEFAULT) { listState, transition ->
        when (transition) {
            LoadingPatterns -> listState.copy(loadingState = LoadingState.LOADING)
            is TagsChange -> listState.copy(tagsState = transition.tagsState)
            is PatternsLoaded -> listState.copy(
                listPatterns = transition.listPatterns,
                isHotPatterns = transition.isDefault,
                loadingState = LoadingState.COMPLETE,
                tagsState = transition.tagsState
            )
        }
    }
}
