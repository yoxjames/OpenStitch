package com.yoxjames.openstitch.pattern.ds

import arrow.core.firstOrNone
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import arrow.core.some
import arrow.core.toOption
import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.pattern.api.models.RavelryCraft
import com.yoxjames.openstitch.pattern.api.models.RavelryFullPattern
import com.yoxjames.openstitch.pattern.model.CraftType
import com.yoxjames.openstitch.pattern.model.Free
import com.yoxjames.openstitch.pattern.model.FullPattern
import com.yoxjames.openstitch.pattern.model.Image
import com.yoxjames.openstitch.pattern.model.MonetaryPrice
import com.yoxjames.openstitch.pattern.model.RavelryCraftException
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
        description = notes.toOption(),
        price = when (free) {
            true -> Free.some()
            false -> price.toOption().zip(currency.toOption())
                .map { MonetaryPrice(it.first.toBigDecimal(), it.second) }
        },
        images = patternPhotos.mapNotNull { ravelryPhoto ->
            val imageUrl = sequenceOf(ravelryPhoto.medium2Url, ravelryPhoto.mediumUrl, ravelryPhoto.small2Url, ravelryPhoto.smallUrl)
                .filterNotNull()
                .firstOrNull()
            imageUrl?.let { Image(imageUrl = imageUrl, ravelryPhoto.caption.toOption()) }
        },
        gauge = gauge.toOption().map { it.toString() },
        yardage = "".some(), // TODO
        weight = yarnWeight.toOption().mapNotNull { it.name },
        craftType = craft.toOption().map { it.craftType }.getOrElse { RavelryCraftException.NullCraft.left() },
        usNeedleSize = patternNeedleSizes.toOption().flatMap { it.firstOrNone() }.mapNotNull { it.us },
        metricNeedleSize = patternNeedleSizes.toOption().flatMap { it.firstOrNone() }.mapNotNull { it.prettyMetric }
    )

    private val RavelryCraft.craftType get() = when (name) {
        "Knitting" -> CraftType.KNITTING.right()
        "Crochet" -> CraftType.CROCHET.right()
        else -> RavelryCraftException.InvalidCraft(this).left()
    }
}

sealed interface PatternDetailTransition

object LoadingPattern : PatternDetailTransition

data class PatternLoaded(
    val pattern: FullPattern
) : PatternDetailTransition
