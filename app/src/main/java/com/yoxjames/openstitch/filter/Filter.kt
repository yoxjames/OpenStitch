package com.yoxjames.openstitch.filter

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.list.ListItemState
import com.yoxjames.openstitch.list.ListItemViewEvent
import com.yoxjames.openstitch.list.ListState

fun interface Filter<T> : (Sequence<T>) -> Sequence<T>

fun <T> Sequence<T>.applyFilter(filter: Filter<T>) = filter(this)

@ExperimentalMaterialApi
interface FilterState<T> : Filter<T>, ListItemState {
    val filter: Filter<T>
    val isApplied: Boolean
    val text: String
    override fun invoke(seq: Sequence<T>): Sequence<T> {
        return if (isApplied) seq.applyFilter(filter) else seq
    }
    @Composable
    override fun ItemView(onViewEvent: ViewEventListener<ListItemViewEvent>) {
        ChipViewState(text = text, isApplied = isApplied).ItemContent(viewEventListener = onViewEvent)
    }
}

@ExperimentalMaterialApi
interface FiltersState<T> : Filter<T> {
    val filters: List<FilterState<T>>
    override fun invoke(seq: Sequence<T>): Sequence<T> {
        val aggregatedFilterFunction = (filters as List<Filter<T>>).reduce { acc, filterState ->
            Filter { it.applyFilter(acc).applyFilter(filterState) }
        }
        return seq.applyFilter(aggregatedFilterFunction)
    }
}

@ExperimentalMaterialApi
fun <T> FiltersState<T>.asListState() = ListState(filters)
