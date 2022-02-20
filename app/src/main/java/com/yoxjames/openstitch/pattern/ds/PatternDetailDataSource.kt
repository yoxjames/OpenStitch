package com.yoxjames.openstitch.pattern.ds

import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.pattern.api.models.RavelryCraft
import com.yoxjames.openstitch.pattern.api.models.RavelryFullPattern
import com.yoxjames.openstitch.pattern.model.CraftType
import com.yoxjames.openstitch.pattern.model.Free
import com.yoxjames.openstitch.pattern.model.FullPattern
import com.yoxjames.openstitch.pattern.model.Image
import com.yoxjames.openstitch.pattern.model.MonetaryPrice
import com.yoxjames.openstitch.pattern.model.None
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PatternDetailDataSource @Inject constructor(
    private val patternApiService: PatternApiService
) {
    fun loadPattern(patternId: Long) = flow {
        patternApiService.getFullPattern(patternId).unwrap(
            onSuccess = { emit(PatternLoaded(it.pattern.asFullPattern)) },
            onFailure = { /* TODO */ }
        )
    }.flowOn(Dispatchers.IO)

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
