package com.yoxjames.openstitch.list

import com.yoxjames.openstitch.core.State

@JvmInline
value class ListState(
    val items: List<ListItemState>
) : State {
    fun asViewState(config: ListViewConfiguration = ListViewConfiguration()): ListViewState {
        return ListViewState(
            listLayout = config.layout,
            items = items.map { it.viewState }
        )
    }
}

interface ListItemState : State {
    val viewState: ListItemViewState
}

data class ListViewConfiguration(
    val layout: ListViewState.ListLayout = ListViewState.ListLayout.ROW
)