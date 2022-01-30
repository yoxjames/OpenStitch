package com.yoxjames.openstitch.pattern

import com.yoxjames.openstitch.pattern.api.RavelryListPattern
import java.math.BigDecimal
import java.util.Currency

enum class CraftType {
    KNITTING, CROCHET, UNKNOWN;
}

sealed interface Pattern {
    val id: Long
    val name: String
    val author: String
    val isFree: Boolean
}

data class ListPattern(
    override val id: Long,
    override val name: String,
    override val author: String,
    override val isFree: Boolean,
    val thumbnail: String,
) : Pattern

data class FullPattern(
    override val id: Long,
    override val name: String,
    override val author: String,
    override val isFree: Boolean,
    val craftType: CraftType,
    val price: BigDecimal,
    val currency: String,
    val description: String,
    val images: List<Image>,
    val gauge: String,
    val yardage: String,
    val weight: String,
    val usNeedleSize: String,
    val metricNeedleSize: String
) : Pattern

data class Image(
    val imageUrl: String,
    val caption: String
)

