package com.yoxjames.openstitch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.yoxjames.openstitch.core.ViewEvent
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SearchViewState(
    val hint: String
): ViewState {
    @Composable
    fun Composable(viewEventListener: ViewEventListener<SearchViewEvent>) {
        var text by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()
        var currentJob by remember { mutableStateOf<Job?>(null) }
        val focusRequester = remember { FocusRequester() }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.background,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .focusRequester(focusRequester),
                    value = text,
                    maxLines = 1,
                    singleLine = true,
                    placeholder = { Text(hint) },
                    leadingIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                viewEventListener.onEvent(
                                    BackClicked
                                )
                            }
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { text = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Search"
                            )
                        }
                    },
                    onValueChange = {
                        if (!it.contains("\n")) text = it
                        coroutineScope.launch {
                            viewEventListener.onEvent(SearchTextChanged(it))
                        }
                        currentJob?.cancel()
                        currentJob = coroutineScope.launch {
                            delay(200)
                            viewEventListener.onEvent(SearchEntered)
                        }
                    }
                )
            }
        }
        DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose {  }
        }
    }
}

sealed interface SearchViewEvent : ViewEvent

object BackClicked : SearchViewEvent

data class SearchTextChanged(
    val text: String
) : SearchViewEvent

object SearchEntered : SearchViewEvent

@Preview
@Composable
fun SimpleSearchBar() {
    SearchViewState("Search Patterns").Composable { }
}
