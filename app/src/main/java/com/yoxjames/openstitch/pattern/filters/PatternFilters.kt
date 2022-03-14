package com.yoxjames.openstitch.pattern.filters

import androidx.compose.material.ExperimentalMaterialApi
import com.yoxjames.openstitch.filter.Filter
import com.yoxjames.openstitch.filter.FilterState
import com.yoxjames.openstitch.filter.FiltersState
import com.yoxjames.openstitch.pattern.model.ListPattern

fun interface PatternFilter : Filter<ListPattern>

@ExperimentalMaterialApi
data class PatternFilterState(
    override val filter: PatternFilter,
    override val text: String,
    override val isApplied: Boolean,
) : FilterState<ListPattern>

@ExperimentalMaterialApi
data class PatternFiltersState(
    override val filters: List<PatternFilterState>
) : FiltersState<ListPattern>
