package com.yoxjames.openstitch.pattern.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RavelryFullPattern(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("pattern_author") val patternAuthor: RavelryPatternAuthor,
    @SerialName("photos") val patternPhotos: List<RavelryPhoto>,
    @SerialName("gauge") val gauge: Double?,
    @SerialName("gauge_description") val guageDescription: String?,
    @SerialName("notes_html") val notes: String?,
    @SerialName("price") val price: Double?,
    @SerialName("currency") val currency: String?,
    @SerialName("free") val free: Boolean,
    @SerialName("yarn_weight") val yarnWeight: RavelryYarnWeight?,
    @SerialName("pattern_needle_sizes") val patternNeedleSizes: List<RavelryPatternNeedleSize>,
    @SerialName("craft") val craft: RavelryCraft
)
