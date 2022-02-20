package com.yoxjames.openstitch.pattern

import com.yoxjames.openstitch.loading.LoadingState
import com.yoxjames.openstitch.pattern.ds.HotPatternsLoaded
import com.yoxjames.openstitch.pattern.ds.LoadingPatterns
import com.yoxjames.openstitch.pattern.ds.PatternListDataSource
import com.yoxjames.openstitch.pattern.ds.PatternSearchLoaded
import com.yoxjames.openstitch.pattern.state.PatternListState
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@FlowPreview
@ActivityScoped
class PatternsFlowFactory @Inject constructor(
    private val patternListDataSource: PatternListDataSource,
    private val coroutineScope: CoroutineScope,
) {
}

