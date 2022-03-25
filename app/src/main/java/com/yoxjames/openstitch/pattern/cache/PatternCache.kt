package com.yoxjames.openstitch.pattern.cache

import com.yoxjames.openstitch.pattern.api.models.RavelryListPattern
import com.yoxjames.openstitch.pattern.cache.models.PatternSearchCachedList
import com.yoxjames.openstitch.pattern.ds.PatternSearchParams
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class PatternCache @Inject constructor() {
    private companion object {
        private const val CACHE_TIME_MILLIS = 1000 * 60 * 1
    }

    private val patternSearchMemCache: MutableMap<PatternSearchParams, PatternSearchCachedList> = mutableMapOf()

    fun isCached(patternSearchParams: PatternSearchParams): Boolean {
        return patternSearchMemCache.containsKey(patternSearchParams)
    }

    fun isOutOfDate(patternSearchParams: PatternSearchParams): Boolean {
        val isOutOfDate = (patternSearchMemCache[patternSearchParams]?.expiresAtMillis ?: 0) <= System.currentTimeMillis()
        Timber.d("isOutOfDate? $isOutOfDate, expiresAt ${patternSearchMemCache[patternSearchParams]?.expiresAtMillis}")

        return isOutOfDate
    }

    fun getSearchPatterns(patternSearchParams: PatternSearchParams): List<RavelryListPattern> {
        return patternSearchMemCache[patternSearchParams]!!.patternList
    }

    fun cacheSearchPatterns(patternSearchParams: PatternSearchParams, patternList: List<RavelryListPattern>) {
        patternSearchMemCache[patternSearchParams] = PatternSearchCachedList(
            expiresAtMillis = System.currentTimeMillis() + CACHE_TIME_MILLIS,
            patternList = patternList,
        )
    }
}
