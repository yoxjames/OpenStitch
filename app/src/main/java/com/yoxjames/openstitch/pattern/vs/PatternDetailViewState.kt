package com.yoxjames.openstitch.pattern.vs

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.glide.GlideImage
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.ui.core.ScreenViewEvent
import com.yoxjames.openstitch.ui.generic.QuickInfoCardViewState

data class PatternPhoto(
    val url: String,
    val caption: String
)

data class PatternDetailViewState(
    val name: String,
    val author: String,
    val gallery: List<PatternPhoto>,
    val description: String,
    val quickInfoCards: List<QuickInfoCardViewState>,
) {
    @ExperimentalPagerApi
    @Composable
    fun Composable(viewEventListener: ViewEventListener<ScreenViewEvent>) {
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
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GlideImage(
                        modifier = Modifier.height(300.dp), // Make Percentage
                        imageModel = gallery.map { it.url }[page],
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
            HorizontalPagerIndicator(modifier = Modifier.padding(8.dp), pagerState = pagerState)

            val caption = gallery[pagerState.currentPage].caption

            // Caption
            if (gallery.any { it.caption.isNotBlank() }) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = caption
                )
            }

            FlowRow {
                quickInfoCards.forEach { it.Composable() }
            }

            Surface(modifier = Modifier.padding(horizontal = 8.dp)) {
                Column {
                    Row {
                        Text(text = name, style = MaterialTheme.typography.h5)
                    }
                    Text(text = author, style = MaterialTheme.typography.h6)
                    HtmlText(modifier = Modifier.padding(top = 8.dp), html = description)
                }
            }
        }
    }

    @Composable
    private fun HtmlText(html: String, modifier: Modifier = Modifier) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                TextView(context).apply {
                    // Magic to make the links go!
                    movementMethod = LinkMovementMethod.getInstance()
                }
            },
            update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
        )
    }
}
