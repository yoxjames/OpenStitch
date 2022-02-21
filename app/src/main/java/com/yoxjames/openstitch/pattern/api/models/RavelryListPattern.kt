package com.yoxjames.openstitch.pattern.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RavelryListPattern(
    @SerialName("designer") val designer: RavelryPatternAuthor,
    @SerialName("first_photo") val firstPhoto: RavelryPhoto?,
    @SerialName("free") val free: Boolean,
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("pattern_author") val patternAuthor: RavelryPatternAuthor,
    @SerialName("permalink") val permalink: String,
    @SerialName("personal_attributes") val personalAttributes: String?
)
