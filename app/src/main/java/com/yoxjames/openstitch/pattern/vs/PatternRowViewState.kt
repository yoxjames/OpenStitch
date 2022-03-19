package com.yoxjames.openstitch.pattern.vs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.skydoves.landscapist.glide.GlideImage
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.list.Click
import com.yoxjames.openstitch.list.ListItemViewEvent
import com.yoxjames.openstitch.list.ListItemViewState
import kotlinx.coroutines.launch

data class PatternRowViewState(
    val name: String,
    val author: String,
    val imageUrl: String,
    val isLoading: Boolean
) : ListItemViewState {
    @ExperimentalMaterialApi
    @Composable
    override fun ItemContent(viewEventListener: ViewEventListener<ListItemViewEvent>) {
        val coroutineScope = rememberCoroutineScope()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 2.dp, end = 2.dp),
            elevation = 4.dp,
            onClick = { coroutineScope.launch { viewEventListener.onEvent(Click) } }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                GlideImage(
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                        .align(CenterHorizontally)
                        .patternPlaceholder(),
                    imageModel = imageUrl,
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                ) {
                    Text(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp).patternPlaceholder(), text = name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp).patternPlaceholder(), text = "by $author", maxLines = 1, overflow = TextOverflow.Ellipsis)
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
