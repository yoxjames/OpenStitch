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
    suspend fun getFullPattern(@Path("id") id: String): FullPatternWrapper
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
    @SerialName("name") val name: String,
    @SerialName("pattern_author") val patternAuthor: RavelryPatternAuthor,
    @SerialName("photos") val patternPhotos: List<RavelryPhoto>,
    @SerialName("gauge") val gauge: Double?,
    // TODO: More stuff....
)

@Serializable
data class RavelryListPattern(
    @SerialName("designer") val designer: RavelryPatternAuthor,
    @SerialName("first_photo") val firstPhoto: RavelryPhoto?,
    @SerialName("free") val free: Boolean,
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("pattern_author") val patternAuthor: RavelryPatternAuthor,
    @SerialName("permalink") val permalink: String,
    @SerialName("personal_attributes") val personalAttributes: String?
)

@Serializable
data class RavelryPatternAuthor(
    @SerialName("crochet_pattern_count") val crochetPatternCount: Int,
    @SerialName("favorites_count") val favoritesCount: Int,
    @SerialName("id") val id: Int,
    @SerialName("knitting_pattern_count") val knittingPatternCount: Int,
    @SerialName("name") val name: String,
    @SerialName("patterns_count") val patternsCount: Int,
    @SerialName("permalink") val permalink: String,
    //@SerialName("users") val users: List<>
)

@Serializable
data class RavelryPhoto(
    @SerialName("id") val id: Int,
    @SerialName("medium2_url") val medium2Url: String,
    @SerialName("medium_url") val mediumUrl: String,
    @SerialName("small2_url") val small2Url: String,
    @SerialName("small_url") val smallUrl: String,
    @SerialName("square_url") val squareUrl: String,
    @SerialName("thumbnail_url") val thumbnailUrl: String
)