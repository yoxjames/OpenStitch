package com.yoxjames.openstitch.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState
import com.yoxjames.openstitch.ui.core.TopBarScreenViewEvent
import kotlinx.coroutines.launch

sealed interface TopBarViewState : ViewState {
    @Composable
    fun Composable(viewEventListener: ViewEventListener<TopBarViewEvent>)
}

data class DropDownItemViewState(
    val icon: ImageVector,
    val text: String
)

data class DefaultTopBarViewState(
    val isSearchAvailable: Boolean = false,
    val isBackAvailable: Boolean = false,
    val dropDownMenu: List<DropDownItemViewState> = emptyList()
) : TopBarViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<TopBarViewEvent>) {
        val coroutineScope = rememberCoroutineScope()
        TopAppBar(
            title = { Text("OpenStitch") },
            actions = {
                SearchAction(viewEventListener)
                DropDown(viewEventListener = viewEventListener, dropDownMenu = dropDownMenu)
            },
            navigationIcon = when (isBackAvailable) {
                true -> {
                    {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewEventListener.onEvent(
                                        TopBarBackClick
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    }
                }
                false -> null
            }
        )
    }

    @Composable
    private fun DropDown(
        viewEventListener: ViewEventListener<TopBarViewEvent>,
        dropDownMenu: List<DropDownItemViewState>
    ) {
        if (dropDownMenu.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()
            IconButton(onClick = { expanded = !expanded }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Actions")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = !expanded }) {
                dropDownMenu.forEach {
                    DropdownMenuItem(
                        onClick = { coroutineScope.launch { viewEventListener.onEvent(ActionClick(it.text)) } },
                        modifier = Modifier.wrapContentWidth(unbounded = true)
                    ) {
                        Icon(imageVector = it.icon, contentDescription = "", modifier = Modifier.padding(end = 4.dp))
                        Text(text = it.text, maxLines = 1)
                    }
                }
            }
        }
    }

    @Composable
    private fun SearchAction(viewEventListener: ViewEventListener<TopBarViewEvent>) {
        val coroutineScope = rememberCoroutineScope()
        if (isSearchAvailable) {
            IconButton(
                onClick = { coroutineScope.launch { viewEventListener.onEvent(SearchClick) } }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        }
    }
}

data class SearchTopBarViewState(
    private val searchViewState: SearchViewState
) : TopBarViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<TopBarViewEvent>) {
        TopAppBar {
            searchViewState.Composable {
                viewEventListener.onEvent(TopBarSearchViewEvent(it))
            }
        }
    }
}

sealed interface TopBarViewEvent : TopBarScreenViewEvent

object SearchClick : TopBarViewEvent

object TopBarBackClick : TopBarViewEvent

@JvmInline
value class ActionClick(
    val text: String
) : TopBarViewEvent

@JvmInline
value class TopBarSearchViewEvent(
    val searchViewEvent: SearchViewEvent
) : TopBarViewEvent
