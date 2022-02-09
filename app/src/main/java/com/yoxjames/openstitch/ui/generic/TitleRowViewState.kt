package com.yoxjames.openstitch.ui.generic

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.list.ListItemState
import com.yoxjames.openstitch.list.ListItemViewEvent
import com.yoxjames.openstitch.list.ListItemViewState

data class TitleRowState(
    val text: String
) : ListItemState {
    override val viewState = TitleRowViewState(text)
}

data class TitleRowViewState(
    val text: String
) : ListItemViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ListItemViewEvent>) {
        Text(text = text, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.h4)
    }
}
