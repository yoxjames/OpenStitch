package com.yoxjames.openstitch.pattern

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import com.yoxjames.openstitch.R
import com.yoxjames.openstitch.detail.ContentState
import com.yoxjames.openstitch.detail.ContentViewState
import com.yoxjames.openstitch.detail.EmptyContentViewState
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.ui.generic.QuickInfoCardViewState
import com.yoxjames.openstitch.ui.generic.QuickInfoComposableVectorIcon
import com.yoxjames.openstitch.ui.generic.QuickInfoDrawableIcon
import com.yoxjames.openstitch.ui.pattern.PatternDetailViewState
import com.yoxjames.openstitch.ui.pattern.PatternPhoto
import kotlinx.coroutines.flow.scan
import java.text.NumberFormat
import java.util.Currency
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
    companion object {
        val freeCard = QuickInfoCardViewState(
            icon = QuickInfoComposableVectorIcon(Icons.Default.ShoppingCart),
            firstLine = "Free",
            secondLine = ""
        )
    }
    override val loadingState: LoadingState = LoadingState.COMPLETE
    override val viewState by lazy {
        PatternDetailViewState(
            name = pattern.name,
            author = pattern.author,
            description = pattern.description,
            gallery = pattern.images.map { PatternPhoto(it.imageUrl, it.caption) },
            quickInfoCards = quickInfoCards
        )
    }

    private val quickInfoCards: List<QuickInfoCardViewState> get() = sequence {

        when (pattern.price) {
            Free -> yield(freeCard)
            is MonetaryPrice -> yield(priceCard)
            None -> Unit
        }
        yield(needleSizeCard)
        yield(sizeCard)
        yield(idCard)
    }.toList()

    private val isKnitting = pattern.craftType == CraftType.KNITTING
    private val usSizeLine = when (pattern.usNeedleSize.isBlank()) {
        true -> "No Size"
        false -> "US ${pattern.usNeedleSize}"
    }
    private val metricLine = when(pattern.metricNeedleSize.isBlank()) {
        true -> ""
        false -> "${pattern.metricNeedleSize} mm"
    }
    private val needleSizeCard = QuickInfoCardViewState(
        icon = QuickInfoDrawableIcon(if(isKnitting) R.drawable.knitting_needles else R.drawable.crochet_hook),
        firstLine = usSizeLine,
        secondLine = metricLine
    )

    private val sizeCard = QuickInfoCardViewState(
        icon = QuickInfoDrawableIcon(R.drawable.yarn),
        firstLine = pattern.weight,
        secondLine = ""
    )

    private val priceCard: QuickInfoCardViewState get() {
        val formattedPrice = NumberFormat.getCurrencyInstance().format((pattern.price as MonetaryPrice).price).drop(1)
        val currencySymbol = Currency.getInstance(pattern.currency.ifBlank { "USD" }).symbol
        val prettyPrice = "$currencySymbol$formattedPrice"
        return QuickInfoCardViewState(
            icon = QuickInfoComposableVectorIcon(Icons.Default.ShoppingCart),
            firstLine = prettyPrice,
            secondLine = pattern.currency
        )
    }

    private val idCard = QuickInfoCardViewState(
        icon = QuickInfoComposableVectorIcon(Icons.Default.Settings),
        firstLine = pattern.id.toString(),
        secondLine = ""
    )
}