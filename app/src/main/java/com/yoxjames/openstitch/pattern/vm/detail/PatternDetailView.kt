package com.yoxjames.openstitch.pattern.vm.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.pager.ExperimentalPagerApi
import com.yoxjames.openstitch.core.openStitchActivity
import com.yoxjames.openstitch.ui.core.OpenStitchScaffold

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PatternDetailView() {
    val viewModel = LocalContext.current.openStitchActivity.patternDetailViewModel
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
