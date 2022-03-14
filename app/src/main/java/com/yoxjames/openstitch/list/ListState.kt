package com.yoxjames.openstitch.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import com.google.accompanist.flowlayout.FlowRow
import com.yoxjames.openstitch.core.StatefulViewEvent
import com.yoxjames.openstitch.core.ViewEventListener

data class ListState(
    val items: List<ListItemState>,
) : ListItemState {
    @Composable
    override fun ItemView(onViewEvent: ViewEventListener<ListItemViewEvent>) {
        FlowRow {
            items.forEach { listItemState ->
                listItemState.ItemView {
                    onViewEvent.onEvent(ChildViewEvent(StatefulListItemViewEvent(viewEvent = it, state = listItemState)))
                }
            }
        }
    }
}

@Composable
fun ListState.FlowingRow(viewEventListener: ViewEventListener<StatefulListItemViewEvent>) {
    FlowRow {
        items.forEach { listItemState ->
            listItemState.ItemView {
                viewEventListener.onEvent(StatefulListItemViewEvent(viewEvent = it, state = listItemState))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListState.GridView(scrollState: LazyListState, viewEventListener: ViewEventListener<StatefulListItemViewEvent>) {
    LazyVerticalGrid(state = scrollState, cells = GridCells.Fixed(2)) {
        itemsIndexed(items) { index, item ->
            item.ItemView { viewEventListener.onEvent(StatefulListItemViewEvent(viewEvent = it, state = item)) }
        }
    }
}

interface ListItemState {
    @Composable
    fun ItemView(onViewEvent: ViewEventListener<ListItemViewEvent>)
}

data class StatefulListItemViewEvent(
    override val viewEvent: ListItemViewEvent,
    override val state: ListItemState
) : StatefulViewEvent<ListItemViewEvent, ListItemState>
