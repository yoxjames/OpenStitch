package com.yoxjames.openstitch.filter

import androidx.compose.runtime.Composable
import com.yoxjames.openstitch.core.ViewEventListener
import com.yoxjames.openstitch.list.ListItemState
import com.yoxjames.openstitch.list.ListItemViewEvent
import com.yoxjames.openstitch.list.ListState

enum class Tag {
    Free, Crochet, Knitting;
}

val Tag.excludes get() = when (this) {
    Tag.Free -> emptySet()
    Tag.Crochet -> setOf(Tag.Knitting)
    Tag.Knitting -> setOf(Tag.Crochet)
}

data class TagState(
    val tag: Tag,
    val isApplied: Boolean,
    val text: String = tag.name
) : ListItemState {
    @Composable
    override fun ItemView(onViewEvent: ViewEventListener<ListItemViewEvent>) {
        ChipViewState(text = text, isApplied = isApplied).ItemContent(viewEventListener = onViewEvent)
    }
}

typealias TagsState = List<TagState>

val DefaultTagState = Tag.values().map { TagState(tag = it, isApplied = false) }

fun TagsState.toggle(tag: Tag): TagsState = map { if (it.tag == tag) it.copy(isApplied = !it.isApplied) else it }

fun TagsState.asListState(): ListState {
    val activeExclusions = filter { it.isApplied }.flatMap { it.tag.excludes }
    return ListState(filterNot { activeExclusions.contains(it.tag) })
}
