package com.yoxjames.openstitch.pattern.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RavelryYarnWeight(
    @SerialName("name") val name: String?
)
