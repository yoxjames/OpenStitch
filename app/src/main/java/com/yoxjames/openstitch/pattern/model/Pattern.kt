package com.yoxjames.openstitch.pattern.model

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
    val craftType: CraftType,
    val price: Price,
    val currency: String,
    val description: String,
    val images: List<Image>,
    val gauge: String,
    val yardage: String,
    val weight: String,
    val usNeedleSize: String,
    val metricNeedleSize: String
) : Pattern
