package com.yoxjames.openstitch.pattern.vs.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import arrow.core.Option
import arrow.core.filterOption
import arrow.core.some
import com.yoxjames.openstitch.R
import com.yoxjames.openstitch.pattern.model.CraftType
import com.yoxjames.openstitch.pattern.model.Free
import com.yoxjames.openstitch.pattern.model.MonetaryPrice
import com.yoxjames.openstitch.pattern.state.LoadedPatternDetailState
import com.yoxjames.openstitch.pattern.vs.PatternDetailViewState
import com.yoxjames.openstitch.pattern.vs.PatternPhoto
import com.yoxjames.openstitch.ui.generic.QuickInfoCardViewState
import com.yoxjames.openstitch.ui.generic.QuickInfoComposableVectorIcon
import com.yoxjames.openstitch.ui.generic.QuickInfoDrawableIcon
import java.text.NumberFormat

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
        pattern.price.map { price ->
            when (price) {
                Free -> yield(freeCard)
                is MonetaryPrice -> yield(price.priceCard)
            }
        }
        yield(needleSizeCard)
        yield(sizeCard)
        yield(idCard)
    }.filterOption().toList()

    private val LoadedPatternDetailState.needleSizeCard: Option<QuickInfoCardViewState> get() = pattern.craftType.map { craftType ->
        val usSizeLine = pattern.usNeedleSize.map { "US $it" }
        val metricLine = pattern.metricNeedleSize.map { "$it mm" }
        val res = when (craftType) {
            CraftType.KNITTING -> R.drawable.knitting_needles
            CraftType.CROCHET -> R.drawable.crochet_hook
        }

        QuickInfoCardViewState(
            icon = QuickInfoDrawableIcon(res),
            textLines = listOf(usSizeLine, metricLine).filterOption().ifEmpty { listOf("No Size") }
        )
    }.orNone()

    private val LoadedPatternDetailState.sizeCard: Option<QuickInfoCardViewState> get() = pattern.weight.map { weight ->
        QuickInfoCardViewState(
            icon = QuickInfoDrawableIcon(R.drawable.yarn),
            textLines = listOf(weight)
        )
    }

    private val freeCard = QuickInfoCardViewState(
        icon = QuickInfoComposableVectorIcon(Icons.Default.ShoppingCart),
        textLines = listOf("Free"),
    ).some()

    private val MonetaryPrice.priceCard: Option<QuickInfoCardViewState> get() {
        val formattedPrice = NumberFormat.getCurrencyInstance().format(price)
        return QuickInfoCardViewState(
            icon = QuickInfoComposableVectorIcon(Icons.Default.ShoppingCart),
            textLines = listOf(formattedPrice)
        ).some()
    }

    private val LoadedPatternDetailState.idCard get() = QuickInfoCardViewState(
        icon = QuickInfoComposableVectorIcon(Icons.Default.Settings),
        textLines = listOf(pattern.id.toString())
    ).some()
}
