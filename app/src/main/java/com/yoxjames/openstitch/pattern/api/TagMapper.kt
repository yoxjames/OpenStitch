package com.yoxjames.openstitch.pattern.api

import androidx.compose.material.ExperimentalMaterialApi
import com.yoxjames.openstitch.filter.Tag
import com.yoxjames.openstitch.filter.TagState
import com.yoxjames.openstitch.search.SearchState

fun SearchState.asQueryParams() = when (text.isBlank()) {
    true -> mapOf("sort" to "recently-popular")
    false -> mapOf("query" to text)
}

val Map<String, String>.isHotPatterns get() = containsValue("recently-popular")

fun Tag.asQueryParam(): Pair<String, String> = when (this) {
    Tag.Free -> Pair("availability", "free")
    Tag.Crochet -> Pair("craft", "crochet")
    Tag.Knitting -> Pair("craft", "knitting")
}

@ExperimentalMaterialApi
fun List<TagState>.asQueryParams(): Map<String, String> = filter { it.isApplied }.map { it.tag }.associate { it.asQueryParam() }
