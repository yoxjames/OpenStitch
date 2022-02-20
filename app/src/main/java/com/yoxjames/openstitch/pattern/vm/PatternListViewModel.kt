package com.yoxjames.openstitch.pattern.vm

import com.yoxjames.openstitch.pattern.ds.PatternListDataSource
import com.yoxjames.openstitch.pattern.state.asState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class PatternListViewModel @Inject constructor(
    private val patternListDataSource: PatternListDataSource,
    private val coroutineScope: CoroutineScope
) {
    fun contentState(searchTerm: String = "") = flow {
        if (searchTerm.isBlank()) {
            emitAll(patternListDataSource.loadHotPatterns())
        } else {
            emitAll(patternListDataSource.searchPatterns(searchTerm))
        }
    }.asState().shareIn(coroutineScope, SharingStarted.Lazily, replay = 1)
}