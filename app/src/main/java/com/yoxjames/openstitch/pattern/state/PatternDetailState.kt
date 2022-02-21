package com.yoxjames.openstitch.pattern.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.pattern.ds.LoadingPattern
import com.yoxjames.openstitch.pattern.ds.PatternDetailTransition
import com.yoxjames.openstitch.pattern.ds.PatternLoaded
import com.yoxjames.openstitch.pattern.model.FullPattern
import com.yoxjames.openstitch.ui.generic.QuickInfoCardViewState
import com.yoxjames.openstitch.ui.generic.QuickInfoComposableVectorIcon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.scan

sealed interface PatternDetailState {
    val loadingState: LoadingState
}

object LoadingPatternState : PatternDetailState {
    override val loadingState: LoadingState = LoadingState.LOADING
}

data class LoadedPatternDetailState(
    val pattern: FullPattern
) : PatternDetailState {
    companion object {
        val freeCard = QuickInfoCardViewState(
            icon = QuickInfoComposableVectorIcon(Icons.Default.ShoppingCart),
            firstLine = "Free",
            secondLine = ""
        )
    }
    override val loadingState: LoadingState = LoadingState.COMPLETE
}

fun Flow<PatternDetailTransition>.asState(): Flow<PatternDetailState> {
    return scan<PatternDetailTransition, PatternDetailState>(LoadingPatternState) { state, transition ->
        when (transition) {
            LoadingPattern -> LoadingPatternState
            is PatternLoaded -> LoadedPatternDetailState(transition.pattern)
        }
    }
}
