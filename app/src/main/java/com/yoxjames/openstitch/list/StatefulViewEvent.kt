package com.yoxjames.openstitch.list

import com.yoxjames.openstitch.core.ViewEvent

sealed interface StatePayload

object NoState : StatePayload

data class StatefulListViewEvent(
    val viewEvent: ListItemViewEvent,
    val state: ListItemState,
) : ViewEvent