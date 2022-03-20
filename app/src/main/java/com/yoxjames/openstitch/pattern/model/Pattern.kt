package com.yoxjames.openstitch.pattern.model

import arrow.core.Either
import arrow.core.Option

sealed interface Pattern {
    val id: Long
    val name: String
    val author: String
}

data class ListPattern(
    override val id: Long,
    override val name: String,
    override val author: String,
    val isFree: Boolean,
    val thumbnail: String,
) : Pattern

data class FullPattern(
    override val id: Long,
    override val name: String,
    override val author: String,
    val craftType: Either<RavelryCraftException, CraftType>,
    val price: Option<Price>,
    val description: Option<String>,
    val images: List<Image>,
    val gauge: Option<String>,
    val yardage: Option<String>,
    val weight: Option<String>,
    val usNeedleSize: Option<String>,
    val metricNeedleSize: Option<String>
) : Pattern
