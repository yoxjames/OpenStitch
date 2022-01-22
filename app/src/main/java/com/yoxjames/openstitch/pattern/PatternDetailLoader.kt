package com.yoxjames.openstitch.pattern

import com.yoxjames.openstitch.detail.ContentState
import com.yoxjames.openstitch.detail.ContentViewState
import com.yoxjames.openstitch.detail.EmptyContentViewState
import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.ui.pattern.PatternDetailViewState
import com.yoxjames.openstitch.ui.pattern.PatternPhoto
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PatternDetailLoader @Inject constructor(
    private val patternApiService: PatternApiService,
) {
    fun getFullPattern(patternId: Int) = flow {
        emit(LoadingPatternState)
        val fullPattern = patternApiService.getFullPattern(patternId.toString()).pattern
        emit(
            LoadedPatternDetailState(
                name = fullPattern.name,
                author = fullPattern.patternAuthor.name,
                images = fullPattern.patternPhotos.map { it.medium2Url },
                gauge = fullPattern.gauge?.toString() ?: "UNKNOWN",
                yardage = "TEST",
                weight = "TEST"
            )
        )
    }
}

sealed interface PatternDetailState : ContentState {
    val loadingState: LoadingState
}

object LoadingPatternState : PatternDetailState {
    override val viewState: ContentViewState = EmptyContentViewState
    override val loadingState: LoadingState = LoadingState.LOADING
}

data class LoadedPatternDetailState(
    val name: String,
    val author: String,
    val images: List<String>,
    val gauge: String,
    val yardage: String,
    val weight: String
) : PatternDetailState {
    override val loadingState: LoadingState = LoadingState.COMPLETE
    override val viewState: ContentViewState = PatternDetailViewState(
        patternName = name,
        patternAuthor = author,
        patternGallery = images.map { PatternPhoto(it, "Caption") },
        gauge = gauge,
        yardage = yardage,
        weight = weight
    )
}