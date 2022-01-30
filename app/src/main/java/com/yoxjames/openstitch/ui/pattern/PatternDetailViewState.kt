package com.yoxjames.openstitch.ui.pattern

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
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
    val price: String,
    val gauge: String,
    val yardage: String,
    val weight: String
): ContentViewState {
    @ExperimentalPagerApi
    @Composable
    override fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>) {
        val imageCount = gallery.count()
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pagerState = rememberPagerState()
            HorizontalPager(count = imageCount, state = pagerState) { page ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GlideImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp), // Make Percentage
                        imageModel = gallery.map { it.url }[page]
                    )
                }
            }
            HorizontalPagerIndicator(modifier = Modifier.padding(4.dp), pagerState = pagerState)

            val caption = gallery[pagerState.currentPage].caption
            if (gallery.any { it.caption.isNotBlank() }) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = caption
                )
            }
            Surface(modifier = Modifier.padding(horizontal = 8.dp)) {
                Column {
                    Row {
                        Text(text = name, style = MaterialTheme.typography.h5)
                    }
                    Text(text = author, style = MaterialTheme.typography.h6)
                    Text(text = "Price: $price", fontWeight = FontWeight.Bold)
                    Text(modifier = Modifier.padding(top = 8.dp), text = description)
                }
            }
        }
    }
}

@ExperimentalPagerApi
@Preview
@Composable
fun TestPattern() = PatternDetailViewState(
    name = "Pattern Name",
    author = "Author",
    description = "Description",
    price = "",
    gallery = listOf(),
    gauge = "US 5 -3.75 mm",
    yardage = "190 - 250 yards",
    weight = "Sport"
).Composable { }