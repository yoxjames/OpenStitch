package com.yoxjames.openstitch.pattern.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RavelryCraft(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
)
