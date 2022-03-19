package com.yoxjames.openstitch.pattern.vm.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.yoxjames.openstitch.list.FlowingRow
import com.yoxjames.openstitch.list.GridView
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold

@ExperimentalMaterialApi
@Composable
fun PatternListView(listState: LazyListState, viewModel: PatternListViewModel) {
    val plss: PatternListScreenState = viewModel.state.collectAsState().value
    with(plss) {
        OpenStitchScaffold(
            onTopBarViewEvent = { viewModel.emitViewEvent(PatternListTopBarViewEvent(it)) },
            topBarViewState = searchState.mapToTopBarViewState(),
            loadingViewState = patternListState.loadingState.viewState
        ) {
            Column {
                patternListState.chipList.FlowingRow {
                    viewModel.emitViewEvent(PatternListViewEvent(it))
                }
                patternListState.listState.GridView(
                    scrollState = listState,
                    viewEventListener = { viewModel.emitViewEvent(PatternListViewEvent(it)) }
                )
            }
        }
    }
    LaunchedEffect(true) {
        viewModel.start()
    }
}
