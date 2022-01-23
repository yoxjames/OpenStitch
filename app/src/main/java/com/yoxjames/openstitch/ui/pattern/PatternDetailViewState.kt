package com.yoxjames.openstitch.ui.pattern

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.detail.ContentViewState
import com.yoxjames.openstitch.ui.core.ScreenViewEvent

data class PatternPhoto(
    val url: String,
    val caption: String
)

data class PatternDetailViewState(
    val name: String,
    val author: String,
    val gallery: List<PatternPhoto>,
    val description: String,
    val gauge: String,
    val yardage: String,
    val weight: String
): ContentViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(state = scrollState)
        ) {
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), // Make Percentage
                imageModel = gallery.map { it.url }.firstOrNull()
            )
            Surface(modifier = Modifier.padding(8.dp)) {
                Column {
                    Text(text = name, style = MaterialTheme.typography.h4)
                    Text(text = author, style = MaterialTheme.typography.h5)
                    Text(description)
                }
            }
        }
    }
}

@Preview
@Composable
fun TestPattern() = PatternDetailViewState(
    name = "Pattern Name",
    author = "Author",
    description = "Description",
    gallery = listOf(),
    gauge = "US 5 -3.75 mm",
    yardage = "190 - 250 yards",
    weight = "Sport"
).Composable { }