package com.yoxjames.openstitch.pattern.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PatternApiService {
    @GET("patterns/search.json")
    suspend fun search(
        @Query("query") query: String? = null,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("personal_attributes") personalAttributes: Boolean? = null,
        @Query("sort") sort: String? = null
    ): SearchResponseWrapper

    @GET("patterns/{id}.json")
    suspend fun getFullPattern(@Path("id") id: Long): FullPatternWrapper
}

@Serializable
data class SearchResponseWrapper(
    @SerialName("patterns") val patterns: List<RavelryListPattern>
)

@Serializable
data class FullPatternWrapper(
    @SerialName("pattern") val pattern: RavelryFullPattern
)

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
    @SerialName("yarn_weight") val yarnWeight: RavelryYarnWeight,
    @SerialName("pattern_needle_sizes") val patternNeedleSizes: List<RavelryPatternNeedleSize>,
    @SerialName("craft") val craft: RavelryCraft
)

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

@Serializable
data class RavelryYarnWeight(
    @SerialName("name") val name: String?
)

@Serializable
data class RavelryPatternNeedleSize(
    @SerialName("id") val id: Long,
    @SerialName("crochet") val crochet: Boolean,
    @SerialName("knitting") val knitting: Boolean,
    @SerialName("name") val name: String?,
    @SerialName("us") val us: String?,
    @SerialName("pretty_metric") val prettyMetric: String?
)

@Serializable
data class RavelryCraft(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
)
