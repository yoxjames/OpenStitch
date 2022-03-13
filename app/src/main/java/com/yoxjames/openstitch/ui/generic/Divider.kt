package com.yoxjames.openstitch.ui.generic

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.list.ListItemState
import com.yoxjames.openstitch.list.ListItemViewEvent

object Divider : ListItemState {
    @Composable
    override fun RowView(onViewEvent: ViewEventListener<ListItemViewEvent>) = Divider()
}
