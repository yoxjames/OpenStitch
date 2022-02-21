package com.yoxjames.openstitch.list

import com.yoxjames.openstitch.core.ViewEvent

data class PositionalListViewEvent(
    val pos: Int,
    val event: ListItemViewEvent
) : ViewEvent

sealed interface ListItemViewEvent : ViewEvent

object Click : ListItemViewEvent
