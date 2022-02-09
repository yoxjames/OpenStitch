package com.yoxjames.openstitch.ui.generic

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

sealed interface QuickInfoIcon

data class QuickInfoDrawableIcon(
    @DrawableRes val drawableRes: Int
) : QuickInfoIcon

data class QuickInfoComposableVectorIcon(
    val vector: ImageVector
) : QuickInfoIcon

data class QuickInfoCardViewState(
    val icon: QuickInfoIcon,
    val firstLine: String,
    val secondLine: String
) {
    @Composable
    fun Composable() {
        if (firstLine.isNotBlank()) {
            QuickInfoCard {
                when (icon) {
                    is QuickInfoComposableVectorIcon -> {
                        Icon(icon.vector, contentDescription = null)
                    }
                    is QuickInfoDrawableIcon -> {
                        Icon(painterResource(id = icon.drawableRes), contentDescription = null)
                    }
                }
                Text(text = firstLine)
                if (secondLine.isNotBlank()) { Text(text = secondLine) }
            }
        }
    }

    @Composable
    private fun QuickInfoCard(Content: @Composable () -> Unit) {
        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(4.dp).height(88.dp).defaultMinSize(minWidth = 88.dp, minHeight = 88.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Content()
            }
        }
    }
}
