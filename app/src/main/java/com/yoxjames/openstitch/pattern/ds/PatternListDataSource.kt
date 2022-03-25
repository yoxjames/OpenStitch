package com.yoxjames.openstitch.pattern.ds

import androidx.compose.material.ExperimentalMaterialApi
import com.yoxjames.openstitch.filter.TagsState
import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.pattern.api.asQueryParams
import com.yoxjames.openstitch.pattern.api.isHotPatterns
import com.yoxjames.openstitch.pattern.api.models.RavelryListPattern
import com.yoxjames.openstitch.pattern.cache.PatternCache
import com.yoxjames.openstitch.pattern.model.ListPattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class PatternListDataSource @Inject constructor(
    private val patternApiService: PatternApiService,
    private val patternCache: PatternCache,
) {
    @ExperimentalMaterialApi
    fun loadPatterns(patternSearchParams: PatternSearchParams) = flow {
        if (patternCache.isCached(patternSearchParams) && !patternCache.isOutOfDate(patternSearchParams)) {
            emit(
                PatternsLoaded(
                    isDefault = patternSearchParams.searchState.asQueryParams().isHotPatterns,
                    tagsState = patternSearchParams.tags,
                    listPatterns = patternCache.getSearchPatterns(patternSearchParams).map(RavelryPatternMapper)
                )
            )
        } else {
            Timber.d("patternSearchParams is not cached and/or is out of date. Hitting network")
            val apiFilters = patternSearchParams.searchState.asQueryParams() + patternSearchParams.tags.asQueryParams()
            patternApiService.search(tags = apiFilters).unwrap(
                onSuccess = {
                    patternCache.cacheSearchPatterns(patternSearchParams, it.patterns)
                    emit(
                        PatternsLoaded(
                            isDefault = patternSearchParams.searchState.asQueryParams().isHotPatterns,
                            tagsState = patternSearchParams.tags,
                            listPatterns = it.patterns.map(RavelryPatternMapper)
                        )
                    )
                },
                onFailure = { }
            )
        }
    }.flowOn(Dispatchers.IO)
}

sealed interface PatternListTransition

@JvmInline
value class TagsChange(
    val tagsState: TagsState
) : PatternListTransition

object LoadingPatterns : PatternListTransition

data class PatternsLoaded(
    val isDefault: Boolean = true,
    val tagsState: TagsState,
    val listPatterns: List<ListPattern>,
) : PatternListTransition

object RavelryPatternMapper : (RavelryListPattern) -> ListPattern {
    override fun invoke(ravelryPattern: RavelryListPattern): ListPattern {
        return ListPattern(
            id = ravelryPattern.id,
            name = ravelryPattern.name,
            author = ravelryPattern.patternAuthor.name,
            thumbnail = ravelryPattern.firstPhoto?.mediumUrl ?: "",
            isFree = ravelryPattern.free
        )
    }
}
