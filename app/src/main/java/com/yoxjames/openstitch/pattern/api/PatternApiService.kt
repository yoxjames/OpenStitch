package com.yoxjames.openstitch.pattern.api

import com.yoxjames.openstitch.pattern.api.models.RavelryFullPattern
import com.yoxjames.openstitch.pattern.api.models.RavelryListPattern
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PatternApiService {
    @GET("patterns/search.json")
    suspend fun search(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("sort") sort: String? = null,
        @QueryMap tags: Map<String, String> = emptyMap()
    ): Response<SearchResponseWrapper>

    @GET("patterns/{id}.json")
    suspend fun getFullPattern(@Path("id") id: Long): Response<FullPatternWrapper>
}

@Serializable
data class FullPatternWrapper(
    @SerialName("pattern") val pattern: RavelryFullPattern
)

@Serializable
data class SearchResponseWrapper(
    @SerialName("patterns") val patterns: List<RavelryListPattern>
)
