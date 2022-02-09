package com.yoxjames.openstitch.list

@JvmInline
value class ListState(
    val items: List<ListItemState>
) {
    fun asViewState(config: ListViewConfiguration = ListViewConfiguration()): ListViewState {
        return ListViewState(
            listLayout = config.layout,
            items = items.map { it.viewState }
        )
    }
}

interface ListItemState {
    val viewState: ListItemViewState
}

data class ListViewConfiguration(
    val layout: ListViewState.ListLayout = ListViewState.ListLayout.ROW
)
