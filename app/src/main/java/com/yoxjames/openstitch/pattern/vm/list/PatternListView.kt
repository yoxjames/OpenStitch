package com.yoxjames.openstitch.pattern.vm.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.yoxjames.openstitch.core.openStitchActivity
import com.yoxjames.openstitch.list.FlowingRow
import com.yoxjames.openstitch.list.GridView
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold

@Composable
fun PatternListView() {
    val listState = rememberLazyListState()
    val viewModel = LocalContext.current.openStitchActivity.patternListViewModel
    val state: PatternListScreenState = viewModel.state.collectAsState().value
    with(state) {
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

