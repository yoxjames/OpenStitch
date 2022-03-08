package com.yoxjames.openstitch.ui.generic

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Keyboard(show: Boolean) {
    if (show) {
        LocalSoftwareKeyboardController.current?.show()
    } else {
        LocalSoftwareKeyboardController.current?.hide()
    }
}