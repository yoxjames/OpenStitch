package com.yoxjames.openstitch.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState

data class ListViewState(
    val listLayout: ListLayout,
    val items: List<ListItemViewState>,
): ViewState {
    enum class ListLayout {
        ROW, GRID, SIDE_SCROLL
    }

    @Composable
    fun Composable(viewEventListener: ViewEventListener<ListViewEvent>) {
        LazyColumn {
            itemsIndexed(items) { index, item ->
                item.Composable { viewEventListener.onEvent(PositionalListViewEvent(pos = index, event = it)) }
            }
        }
    }
}

interface ListItemViewState : ViewState {
    @Composable
    fun Composable(viewEventListener: ViewEventListener<ListItemViewEvent>)
}