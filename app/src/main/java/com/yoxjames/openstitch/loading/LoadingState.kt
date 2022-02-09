package com.yoxjames.openstitch.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yoxjames.openstitch.core.ViewState

enum class LoadingState(val viewState: LoadingViewState) {
    LOADING(viewState = LoadingViewState(showLoadingSpinner = true)),
    COMPLETE(viewState = LoadingViewState(showLoadingSpinner = false));
}

val LoadingState.asBoolean get() = this == LoadingState.LOADING

data class LoadingViewState(
    val showLoadingSpinner: Boolean
) : ViewState {
    @Composable
    fun Composable() {
        if (showLoadingSpinner) {
            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(Modifier)
            }
        }
    }
}
