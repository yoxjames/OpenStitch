package com.yoxjames.openstitch.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.StatefulViewEvent
import com.yoxjames.openstitch.core.ViewEventListener

data class ListState(
    val items: List<ListItemState>,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListState.Composable(scrollState: LazyListState, viewEventListener: ViewEventListener<StatefulListItemViewEvent>) {
    LazyVerticalGrid(state = scrollState, cells = GridCells.Fixed(2)) {
        itemsIndexed(items) { index, item ->
            item.RowView { viewEventListener.onEvent(StatefulListItemViewEvent(viewEvent = it, state = item)) }
        }
    }
}

interface ListItemState {
    @Composable
    fun RowView(onViewEvent: ViewEventListener<ListItemViewEvent>)
}

data class StatefulListItemViewEvent(
    override val viewEvent: ListItemViewEvent,
    override val state: ListItemState
) : StatefulViewEvent<ListItemViewEvent, ListItemState>
