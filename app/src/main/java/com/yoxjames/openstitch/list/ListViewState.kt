package com.yoxjames.openstitch.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState

/**
 * A ViewState that represents a generic List type. It is @Composable and always composes the items
 * within it to a specific layout/strategy depending on the other contents of the ViewStates.
 *
 * All List Views in OpenStitch should be represented as a ListViewState.
 */
data class ListViewState(
    val listLayout: ListLayout,
    val items: List<ListItemViewState>,
): ViewState {

    /**
     * How the list is laid out. Right now only ROW (vertical scrolling list) is implemented.
     */
    enum class ListLayout {
        ROW, GRID, SIDE_SCROLL
    }

    @Composable
    fun Composable(scrollState: LazyListState, viewEventListener: ViewEventListener<ListViewEvent>) {
        LazyColumn(state = scrollState) {
            itemsIndexed(items) { index, item ->
                item.Composable { viewEventListener.onEvent(PositionalListViewEvent(pos = index, event = it)) }
            }
        }
    }
}

/**
 * An item within a ListViewState. Any item rendered in an OpenStitch list should be a ListItemViewState
 */
interface ListItemViewState : ViewState {
    @Composable
    fun Composable(viewEventListener: ViewEventListener<ListItemViewEvent>)
}