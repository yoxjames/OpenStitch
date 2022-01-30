package com.yoxjames.openstitch.pattern

import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.pattern.api.RavelryFullPattern
import com.yoxjames.openstitch.pattern.api.RavelryPatternNeedleSize
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
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
        isFree = free,
        price = price?.toBigDecimal() ?: BigDecimal(0.0),
        currency = currency ?: "",
        images = patternPhotos.map { Image(imageUrl = it.medium2Url, caption = it.caption ?: "") },
        gauge = gauge.toString(),
        yardage = "",
        weight = yarnWeight.name ?: "",
        craftType = patternNeedleSizes.craftType,
        usNeedleSize = patternNeedleSizes.firstOrNull()?.us ?: "",
        metricNeedleSize = patternNeedleSizes.firstOrNull()?.prettyMetric ?: ""
    )

    private val List<RavelryPatternNeedleSize>.craftType get() = when(firstOrNull()?.knitting) {
        true -> CraftType.KNITTING
        false -> CraftType.CROCHET
        null -> CraftType.UNKNOWN
    }
}

sealed interface PatternDetailTransition

object LoadingPattern : PatternDetailTransition

data class PatternLoaded(
    val pattern: FullPattern
) : PatternDetailTransition