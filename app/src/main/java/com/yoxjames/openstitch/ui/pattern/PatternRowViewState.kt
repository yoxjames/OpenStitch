package com.yoxjames.openstitch.ui.pattern

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.skydoves.landscapist.glide.GlideImage
import com.yoxjames.openstitch.list.Click
import com.yoxjames.openstitch.list.ListItemViewEvent
import com.yoxjames.openstitch.list.ListItemViewState
import com.yoxjames.openstitch.core.ViewEventListener
import kotlinx.coroutines.launch

data class PatternRowViewState(
    val name: String,
    val author: String,
    val imageUrl: String,
    val isLoading: Boolean
) : ListItemViewState {
    @ExperimentalMaterialApi
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ListItemViewEvent>) {
        val coroutineScope = rememberCoroutineScope()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            elevation = 4.dp,
            onClick = { coroutineScope.launch { viewEventListener.onEvent(Click) } }
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                GlideImage(
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                        .padding(8.dp)
                        .patternPlaceholder(),
                    imageModel = imageUrl,
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(modifier = Modifier.patternPlaceholder(), text = name, fontWeight = FontWeight.Bold)
                    Text(modifier = Modifier.patternPlaceholder(), text = "by $author")
                }
            }
        }
    }

    private fun Modifier.patternPlaceholder(): Modifier = placeholder(
        visible = isLoading,
        color = Color.Gray,
        highlight = PlaceholderHighlight.shimmer(
            highlightColor = Color.White
        )
    )
}
