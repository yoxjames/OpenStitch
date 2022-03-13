package com.yoxjames.openstitch.list

import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState

/**
 * An item within a ListViewState. Any item rendered in an OpenStitch list should be a ListItemViewState
 */
interface ListItemViewState : ViewState {
    @Composable
    fun Composable(viewEventListener: ViewEventListener<ListItemViewEvent>)
}
