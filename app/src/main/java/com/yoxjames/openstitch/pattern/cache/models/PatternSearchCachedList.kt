package com.yoxjames.openstitch.pattern.cache.models

import com.yoxjames.openstitch.pattern.api.models.RavelryListPattern

data class PatternSearchCachedList(
    val expiresAtMillis: Long,
    val patternList: List<RavelryListPattern>,
)
