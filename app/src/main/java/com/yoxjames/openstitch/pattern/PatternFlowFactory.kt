package com.yoxjames.openstitch.pattern

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import com.yoxjames.openstitch.R
import com.yoxjames.openstitch.detail.ContentState
import com.yoxjames.openstitch.detail.ContentViewState
import com.yoxjames.openstitch.detail.EmptyContentViewState
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.ui.generic.QuickInfoCardViewState
import com.yoxjames.openstitch.ui.generic.QuickInfoComposableVectorIcon
import com.yoxjames.openstitch.ui.generic.QuickInfoDrawableIcon
import com.yoxjames.openstitch.ui.pattern.PatternDetailViewState
import com.yoxjames.openstitch.ui.pattern.PatternPhoto
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.scan
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

class PatternFlowFactory @Inject constructor(
    private val patternService: PatternService
) {
    fun getFullPattern(patternId: Long) = patternService.loadPattern(patternId)
        .scan<PatternDetailTransition, PatternDetailState>(LoadingPatternState) { state, transition ->
            when (transition) {
                LoadingPattern -> LoadingPatternState
                is PatternLoaded -> LoadedPatternDetailState(transition.pattern)
            }
        }
}

sealed interface PatternDetailState : ContentState {
    val loadingState: LoadingState
}

object LoadingPatternState : PatternDetailState {
    override val viewState: ContentViewState = EmptyContentViewState
    override val loadingState: LoadingState = LoadingState.LOADING
}

data class LoadedPatternDetailState(
    val pattern: FullPattern
) : PatternDetailState {
    override val loadingState: LoadingState = LoadingState.COMPLETE
    override val viewState: ContentViewState get() {
        // TODO: I imagine there is a less disgusting way to do this.
        println("ID: ${pattern.id}")
        return PatternDetailViewState(
            name = pattern.name,
            author = pattern.author,
            description = pattern.description,
            gallery = pattern.images.map { PatternPhoto(it.imageUrl, it.caption) },
            quickInfoCards = listOf(priceCard, sizeCard, needleSizeCard)
        )
    }


    private val isKnitting = pattern.craftType == CraftType.KNITTING
    private val needleSizeCard = QuickInfoCardViewState(
        icon = QuickInfoDrawableIcon(if(isKnitting) R.drawable.knitting_needles else R.drawable.crochet_hook),
        firstLine = "US ${pattern.usNeedleSize}",
        secondLine = "${pattern.metricNeedleSize} mm"
    )

    private val sizeCard = QuickInfoCardViewState(
        icon = QuickInfoDrawableIcon(R.drawable.yarn),
        firstLine = pattern.weight,
        secondLine = ""
    )
    private val formattedPrice = NumberFormat.getCurrencyInstance().format(pattern.price).drop(1)
    private val currencySymbol = Currency.getInstance(pattern.currency.ifBlank { "USD" }).symbol
    private val prettyPrice = if (pattern.isFree) "FREE" else "$currencySymbol$formattedPrice"
    private val priceCard = QuickInfoCardViewState(
        icon = QuickInfoComposableVectorIcon(Icons.Default.ShoppingCart),
        firstLine = prettyPrice,
        secondLine = if (!pattern.isFree) pattern.currency else ""
    )
}