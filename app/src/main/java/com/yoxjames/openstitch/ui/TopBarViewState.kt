package com.yoxjames.openstitch.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.yoxjames.openstitch.ui.core.TopBarScreenViewEvent
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState
import kotlinx.coroutines.launch

sealed interface TopBarViewState : ViewState {
    @Composable
    fun Composable(viewEventListener: ViewEventListener<TopBarViewEvent>)
}

data class DefaultTopBarViewState(
    private val isSearchAvailable: Boolean = false,
    private val isBackAvailable: Boolean = false,
) : TopBarViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<TopBarViewEvent>) {
        val coroutineScope = rememberCoroutineScope()
        TopAppBar(
            title = { Text("OpenStitch") },
            actions = { SearchAction(viewEventListener) },
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
): TopBarViewState {
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
value class TopBarSearchViewEvent(
    val searchViewEvent: SearchViewEvent
) : TopBarViewEvent