package com.yoxjames.openstitch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface ColorFamilyService {
    @GET("color_families.json")
    suspend fun fetchColorFamilies(): ColorFamilyWrapper
}

@Serializable
data class ColorFamilyWrapper(
    @SerialName("color_families") val colorFamilies: List<ColorFamily>
)

@Serializable
data class ColorFamily(
    @SerialName("color") val color: String?,
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("permalink") val permalink: String,
    @SerialName("spectrum_order") val spectrumOrder: Long
)
