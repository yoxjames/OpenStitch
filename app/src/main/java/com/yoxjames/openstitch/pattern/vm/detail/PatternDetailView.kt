package com.yoxjames.openstitch.pattern.vm.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.yoxjames.openstitch.ui.ActionClick
import com.yoxjames.openstitch.ui.SearchClick
import com.yoxjames.openstitch.ui.TopBarBackClick
import com.yoxjames.openstitch.ui.TopBarSearchViewEvent
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold

@OptIn(ExperimentalPagerApi::class)
@Destination(navArgsDelegate = PatternDetailNavArgs::class)
@Composable
fun PatternDetailView(navigator: DestinationsNavigator) {
    val viewModel: PatternDetailViewModel = hiltViewModel()
    val state: PatternDetailScreenState = viewModel.state.collectAsState().value
    val viewState = DetailScreenViewStateMapper(state)
    with(viewState) {
        OpenStitchScaffold(
            onTopBarViewEvent = {
                when (it) {
                    is ActionClick,
                    is TopBarSearchViewEvent,
                    SearchClick -> Unit
                    TopBarBackClick -> navigator.popBackStack()
                }
                viewModel.emitViewEvent(PatternDetailTopBarViewEvent(it))
            },
            topBarViewState = topBarViewState,
            loadingViewState = loadingViewState,
        ) {
            when (val contentViewState = viewState.patternContentViewState) {
                is LoadedPatternViewState -> contentViewState.patternDetailViewState.Composable(
                    viewEventListener = { }
                )
                LoadingPatternViewState -> Unit
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.start()
    }
}
