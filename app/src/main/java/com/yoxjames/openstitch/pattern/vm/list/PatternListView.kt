package com.yoxjames.openstitch.pattern.vm.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yoxjames.openstitch.list.Click
import com.yoxjames.openstitch.list.FlowingRow
import com.yoxjames.openstitch.list.GridView
import com.yoxjames.openstitch.pattern.state.PatternRowItemState
import com.yoxjames.openstitch.pattern.vm.destinations.PatternDetailViewDestination
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold

@Destination(start = true)
@Composable
fun PatternListView(navigator: DestinationsNavigator) {
    val viewModel: PatternListViewModel = hiltViewModel()
    val listState = rememberLazyListState()
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
                    viewEventListener = {
                        if (it.state is PatternRowItemState && it.viewEvent is Click) {
                            navigator.navigate(PatternDetailViewDestination.invoke(it.state.listPattern.id))
                        }
                        viewModel.emitViewEvent(PatternListViewEvent(it))
                    }
                )
            }
        }
    }
    LaunchedEffect(true) {
        viewModel.start()
    }
}

