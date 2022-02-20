package com.yoxjames.openstitch.pattern.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RavelryPhoto(
    @SerialName("id") val id: Long,
    @SerialName("caption") val caption: String?,
    @SerialName("medium2_url") val medium2Url: String?,
    @SerialName("medium_url") val mediumUrl: String?,
    @SerialName("small2_url") val small2Url: String?,
    @SerialName("small_url") val smallUrl: String?,
    @SerialName("square_url") val squareUrl: String?,
    @SerialName("thumbnail_url") val thumbnailUrl: String?
)
