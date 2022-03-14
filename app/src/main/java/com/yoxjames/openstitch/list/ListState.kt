package com.yoxjames.openstitch.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
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
fun ListState.ItemView(scrollState: LazyListState, viewEventListener: ViewEventListener<StatefulListItemViewEvent>) {
    LazyColumn(state = scrollState) {
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
