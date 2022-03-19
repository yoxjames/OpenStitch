package com.yoxjames.openstitch.pattern.vm.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold

@ExperimentalPagerApi
@Composable
fun PatternDetailView(viewModel: PatternDetailViewModel) {
    val state: PatternDetailScreenState = viewModel.state.collectAsState().value
    val viewState = DetailScreenViewStateMapper(state)
    with(viewState) {
        OpenStitchScaffold(
            onTopBarViewEvent = { viewModel.emitViewEvent(PatternDetailTopBarViewEvent(it)) },
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
