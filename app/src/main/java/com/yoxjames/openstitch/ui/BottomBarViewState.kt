package com.yoxjames.openstitch.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState
import com.yoxjames.openstitch.ui.core.BottomBarScreenViewEvent

sealed interface BottomBarViewState : ViewState {
    @Composable
    fun Composable(viewEventListener: ViewEventListener<BottomBarViewEvent>)
}

object DefaultBottomBarViewState : BottomBarViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<BottomBarViewEvent>) {
        BottomNavigation {
            BottomNavigationItem(
                icon = { Icon(imageVector = Icons.Filled.List, contentDescription = "Patterns") },
                label = { Text("Patterns") },
                selected = true,
                onClick = {}
            )
            BottomNavigationItem(
                icon = { Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Yarns") },
                label = { Text("Yarns") },
                selected = false,
                onClick = {}
            )
        }
    }
}

object NoBottomBarViewState : BottomBarViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<BottomBarViewEvent>) = Unit
}

sealed interface BottomBarViewEvent : BottomBarScreenViewEvent