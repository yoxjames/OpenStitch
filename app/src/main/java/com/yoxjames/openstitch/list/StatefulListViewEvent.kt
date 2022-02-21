package com.yoxjames.openstitch.list

import com.yoxjames.openstitch.core.ViewEvent

data class StatefulListViewEvent(
    val viewEvent: ListItemViewEvent,
    val state: ListItemState,
) : ViewEvent
