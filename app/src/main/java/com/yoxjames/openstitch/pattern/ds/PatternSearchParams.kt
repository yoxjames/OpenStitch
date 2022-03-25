package com.yoxjames.openstitch.pattern.ds

import com.yoxjames.openstitch.filter.TagState
import com.yoxjames.openstitch.search.SearchState

data class PatternSearchParams(
    val searchState: SearchState,
    val tags: List<TagState>,
)
