package com.yoxjames.openstitch.list

data class ListState(
    val items: List<ListItemState>,
    val config: ListViewConfiguration = ListViewConfiguration()
) {
    val viewState = ListViewState(
        listLayout = config.layout,
        items = items.map { it.viewState }
    )
}

interface ListItemState {
    val viewState: ListItemViewState
}

data class ListViewConfiguration(
    val layout: ListViewState.ListLayout = ListViewState.ListLayout.ROW
)
