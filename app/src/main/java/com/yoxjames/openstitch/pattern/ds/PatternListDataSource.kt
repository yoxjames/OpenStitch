package com.yoxjames.openstitch.pattern.ds

import androidx.compose.material.ExperimentalMaterialApi
import com.yoxjames.openstitch.filter.TagState
import com.yoxjames.openstitch.filter.TagsState
import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.pattern.api.asQueryParams
import com.yoxjames.openstitch.pattern.api.isHotPatterns
import com.yoxjames.openstitch.pattern.api.models.RavelryListPattern
import com.yoxjames.openstitch.pattern.model.ListPattern
import com.yoxjames.openstitch.search.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PatternListDataSource @Inject constructor(
    private val patternApiService: PatternApiService
) {
    @ExperimentalMaterialApi
    fun loadPatterns(searchState: SearchState, tags: List<TagState>) = flow {
        val apiFilters = searchState.asQueryParams() + tags.asQueryParams()
        patternApiService.search(tags = apiFilters).unwrap(
            onSuccess = {
                emit(
                    PatternsLoaded(
                        isDefault = searchState.asQueryParams().isHotPatterns,
                        tagsState = tags,
                        listPatterns = it.patterns.map(RavelryPatternMapper)
                    )
                )
            },
            onFailure = { }
        )
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
            thumbnail = ravelryPattern.firstPhoto?.smallUrl ?: "",
            isFree = ravelryPattern.free
        )
    }
}
