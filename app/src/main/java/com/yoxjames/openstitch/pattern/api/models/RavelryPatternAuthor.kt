package com.yoxjames.openstitch.pattern.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RavelryPatternAuthor(
    @SerialName("crochet_pattern_count") val crochetPatternCount: Int,
    @SerialName("favorites_count") val favoritesCount: Int,
    @SerialName("id") val id: Long,
    @SerialName("knitting_pattern_count") val knittingPatternCount: Int,
    @SerialName("name") val name: String,
    @SerialName("patterns_count") val patternsCount: Int,
    @SerialName("permalink") val permalink: String?,
    // @SerialName("users") val users: List<>
)
