package com.yoxjames.openstitch.pattern

import com.yoxjames.openstitch.pattern.ds.PatternListDataSource
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ActivityScoped
class PatternsFlowFactory @Inject constructor(
    private val patternListDataSource: PatternListDataSource,
    private val coroutineScope: CoroutineScope,
)
