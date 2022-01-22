package com.yoxjames.openstitch.pattern

import com.yoxjames.openstitch.pattern.api.PatternApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PatternsService @Inject constructor(
    private val patternApiService: PatternApiService
) {
    fun loadHotPatterns() = flow {
        emit(LoadingPatterns)
        val hotPatterns = patternApiService.search(sort = "recently-popular").patterns
            .map(RavelryPatternMapper)
        emit(HotPatternsLoaded(hotPatterns))
    }.flowOn(Dispatchers.IO)

    fun searchPatterns(query: String) = flow {
        emit(LoadingPatterns)
        val searchResults = patternApiService.search(query = query).patterns
            .map(RavelryPatternMapper)
        emit(PatternSearchLoaded(searchResults))
    }.flowOn(Dispatchers.IO)
}

sealed interface PatternFlowTransition

object LoadingPatterns : PatternFlowTransition

data class HotPatternsLoaded(
    val patterns: List<Pattern>
) : PatternFlowTransition

data class PatternSearchLoaded(
    val patterns: List<Pattern>
) : PatternFlowTransition