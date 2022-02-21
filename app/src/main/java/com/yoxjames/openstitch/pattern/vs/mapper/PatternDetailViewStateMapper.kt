package com.yoxjames.openstitch.pattern.vs.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import com.yoxjames.openstitch.R
import com.yoxjames.openstitch.pattern.state.LoadedPatternDetailState
import com.yoxjames.openstitch.pattern.model.CraftType
import com.yoxjames.openstitch.pattern.model.Free
import com.yoxjames.openstitch.pattern.model.MonetaryPrice
import com.yoxjames.openstitch.pattern.model.None
import com.yoxjames.openstitch.pattern.vs.PatternDetailViewState
import com.yoxjames.openstitch.pattern.vs.PatternPhoto
import com.yoxjames.openstitch.ui.generic.QuickInfoCardViewState
import com.yoxjames.openstitch.ui.generic.QuickInfoComposableVectorIcon
import com.yoxjames.openstitch.ui.generic.QuickInfoDrawableIcon
import java.text.NumberFormat
import java.util.Currency

object PatternDetailViewStateMapper : (LoadedPatternDetailState) -> PatternDetailViewState {
    override fun invoke(state: LoadedPatternDetailState): PatternDetailViewState {
        return state.toViewState()
    }

    private fun LoadedPatternDetailState.toViewState(): PatternDetailViewState {
        return PatternDetailViewState(
            name = pattern.name,
            author = pattern.author,
            description = pattern.description,
            gallery = pattern.images.map { PatternPhoto(it.imageUrl, it.caption) },
            quickInfoCards = quickInfoCards
        )
    }

    private val LoadedPatternDetailState.quickInfoCards: List<QuickInfoCardViewState> get() = sequence {

        when (pattern.price) {
            Free -> yield(LoadedPatternDetailState.freeCard)
            is MonetaryPrice -> yield(priceCard)
            None -> Unit
        }
        yield(needleSizeCard)
        yield(sizeCard)
        yield(idCard)
    }.toList()

    private val LoadedPatternDetailState.isKnitting get() = pattern.craftType == CraftType.KNITTING
    private val LoadedPatternDetailState.usSizeLine get() = when (pattern.usNeedleSize.isBlank()) {
        true -> "No Size"
        false -> "US ${pattern.usNeedleSize}"
    }
    private val LoadedPatternDetailState.metricLine get() = when (pattern.metricNeedleSize.isBlank()) {
        true -> ""
        false -> "${pattern.metricNeedleSize} mm"
    }
    private val LoadedPatternDetailState.needleSizeCard get() = QuickInfoCardViewState(
        icon = QuickInfoDrawableIcon(if (isKnitting) R.drawable.knitting_needles else R.drawable.crochet_hook),
        firstLine = usSizeLine,
        secondLine = metricLine
    )

    private val LoadedPatternDetailState.sizeCard get() = QuickInfoCardViewState(
        icon = QuickInfoDrawableIcon(R.drawable.yarn),
        firstLine = pattern.weight,
        secondLine = ""
    )

    private val LoadedPatternDetailState.priceCard: QuickInfoCardViewState get() {
        val formattedPrice = NumberFormat.getCurrencyInstance().format((pattern.price as MonetaryPrice).price).drop(1)
        val currencySymbol = Currency.getInstance(pattern.currency.ifBlank { "USD" }).symbol
        val prettyPrice = "$currencySymbol$formattedPrice"
        return QuickInfoCardViewState(
            icon = QuickInfoComposableVectorIcon(Icons.Default.ShoppingCart),
            firstLine = prettyPrice,
            secondLine = pattern.currency
        )
    }

    private val LoadedPatternDetailState.idCard get() = QuickInfoCardViewState(
        icon = QuickInfoComposableVectorIcon(Icons.Default.Settings),
        firstLine = pattern.id.toString(),
        secondLine = ""
    )
}
