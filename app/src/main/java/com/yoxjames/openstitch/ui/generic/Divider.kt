package com.yoxjames.openstitch.ui.generic

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.list.ListItemViewEvent
import com.yoxjames.openstitch.list.ListItemViewState
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.list.ListItemState

object Divider : ListItemViewState, ListItemState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ListItemViewEvent>) = Divider()
    override val viewState = this
}