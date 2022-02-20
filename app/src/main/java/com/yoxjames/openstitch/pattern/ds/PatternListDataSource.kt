package com.yoxjames.openstitch.pattern.ds

import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.pattern.api.models.RavelryListPattern
import com.yoxjames.openstitch.pattern.model.ListPattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PatternListDataSource @Inject constructor(
    private val patternApiService: PatternApiService
) {
    fun loadHotPatterns() = flow {
        patternApiService.search(sort = "recently-popular").unwrap(
            onSuccess = { emit(HotPatternsLoaded(it.patterns.map(RavelryPatternMapper))) },
            onFailure = { /* TODO */ }
        )
    }.flowOn(Dispatchers.IO)

    fun searchPatterns(query: String) = flow {
        patternApiService.search(query = query).unwrap(
            onSuccess = { emit(PatternSearchLoaded(it.patterns.map(RavelryPatternMapper))) },
            onFailure = { /* TODO */ }
        )
    }.flowOn(Dispatchers.IO)
}

sealed interface PatternListTransition

object LoadingPatterns : PatternListTransition

data class HotPatternsLoaded(
    val listPatterns: List<ListPattern>,
) : PatternListTransition

data class PatternSearchLoaded(
    val listPatterns: List<ListPattern>
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
