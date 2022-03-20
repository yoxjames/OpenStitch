package com.yoxjames.openstitch.filter

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.list.Click
import com.yoxjames.openstitch.list.ListItemViewEvent
import com.yoxjames.openstitch.list.ListItemViewState
import kotlinx.coroutines.launch

data class ChipViewState(
    val text: String,
    val isApplied: Boolean
) : ListItemViewState {
    @Composable
    override fun ItemContent(viewEventListener: ViewEventListener<ListItemViewEvent>) {
        val coroutineScope = rememberCoroutineScope()
        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(start = 4.dp),
            backgroundColor = if (isApplied) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
            onClick = { coroutineScope.launch { viewEventListener.onEvent(Click) } }
        ) {
            Text(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp), text = text)
        }
    }
}
