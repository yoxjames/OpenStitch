package com.yoxjames.openstitch.list

import com.yoxjames.openstitch.core.ViewEvent

sealed interface ListItemViewEvent : ViewEvent

object Click : ListItemViewEvent

data class ChildViewEvent<T>(
    val event: T
) : ListItemViewEvent
