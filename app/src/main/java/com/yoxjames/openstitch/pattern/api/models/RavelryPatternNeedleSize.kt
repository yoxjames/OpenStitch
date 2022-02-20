package com.yoxjames.openstitch.pattern.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RavelryPatternNeedleSize(
    @SerialName("id") val id: Long,
    @SerialName("crochet") val crochet: Boolean,
    @SerialName("knitting") val knitting: Boolean,
    @SerialName("name") val name: String?,
    @SerialName("us") val us: String?,
    @SerialName("pretty_metric") val prettyMetric: String?
)
