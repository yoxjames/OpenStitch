package com.yoxjames.openstitch.list

import com.yoxjames.openstitch.core.ViewEvent
import com.yoxjames.openstitch.ui.core.ListScreenViewEvent

sealed interface ListViewEvent : ListScreenViewEvent {
    val pos: Int
}

data class PositionalListViewEvent(
    override val pos: Int,
    val event: ListItemViewEvent
) : ListViewEvent

sealed interface ListItemViewEvent : ViewEvent

object Click : ListItemViewEvent

interface ChildViewEvent : ListItemViewEvent
