package com.yoxjames.openstitch.pattern

import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.pattern.api.RavelryCraft
import com.yoxjames.openstitch.pattern.api.RavelryFullPattern
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PatternService @Inject constructor(
    private val patternApiService: PatternApiService
) {
    fun loadPattern(patternId: Long) = flow {
        emit(LoadingPattern)
        val fullPattern = patternApiService.getFullPattern(patternId).pattern
        emit(PatternLoaded(fullPattern.asFullPattern))
    }

    private val RavelryFullPattern.asFullPattern get() = FullPattern(
        id = id,
        name = name,
        author = patternAuthor.name,
        description = notes ?: "",
        price = when {
            free -> Free
            price == null -> None
            else -> MonetaryPrice(price.toBigDecimal())
        },
        currency = currency ?: "",
        images = patternPhotos.mapNotNull {
            it.medium2Url?.let { image -> Image(imageUrl = image, caption = it.caption ?: "") }
        },
        gauge = gauge.toString(),
        yardage = "",
        weight = yarnWeight.name ?: "",
        craftType = craft.craftType,
        usNeedleSize = patternNeedleSizes.firstOrNull()?.us ?: "",
        metricNeedleSize = patternNeedleSizes.firstOrNull()?.prettyMetric ?: ""
    )

    private val RavelryCraft.craftType get() = when (name) {
        "Knitting" -> CraftType.KNITTING
        "Crochet" -> CraftType.CROCHET
        else -> CraftType.UNKNOWN
    }
}

sealed interface PatternDetailTransition

object LoadingPattern : PatternDetailTransition

data class PatternLoaded(
    val pattern: FullPattern
) : PatternDetailTransition
