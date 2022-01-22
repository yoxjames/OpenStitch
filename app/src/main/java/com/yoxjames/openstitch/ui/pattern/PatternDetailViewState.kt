package com.yoxjames.openstitch.ui.pattern

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.core.ViewState
import com.yoxjames.openstitch.detail.ContentViewState
import com.yoxjames.openstitch.ui.core.DetailScreenViewState
import com.yoxjames.openstitch.ui.core.ScreenViewEvent
import com.yoxjames.openstitch.ui.core.ScreenViewState

data class PatternPhoto(
    val url: String,
    val caption: String
)

data class PatternDetailViewState(
    val patternName: String,
    val patternAuthor: String,
    val patternGallery: List<PatternPhoto>,
    val gauge: String,
    val yardage: String,
    val weight: String
): ContentViewState {
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp), // Make Percentage
                imageModel = patternGallery.map { it.url }.firstOrNull()
            )
            Surface(modifier = Modifier.padding(8.dp)) {
                Column {
                    Text(text = patternName, style = MaterialTheme.typography.h4)
                    Text(text = patternAuthor, style = MaterialTheme.typography.h5)
                }
            }
        }
    }
}

@Preview
@Composable
fun TestPattern() = PatternDetailViewState(
    patternName = "Pattern Name",
    patternAuthor = "Author",
    patternGallery = listOf(),
    gauge = "US 5 -3.75 mm",
    yardage = "190 - 250 yards",
    weight = "Sport"
).Composable { }