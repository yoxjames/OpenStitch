package com.yoxjames.openstitch.pattern

import com.yoxjames.openstitch.pattern.api.RavelryListPattern

data class Pattern(
    val id: Int,
    val name: String,
    val author: String,
    val imageUrl: String,
)

object RavelryPatternMapper : (RavelryListPattern) -> Pattern {
    override fun invoke(ravelryPattern: RavelryListPattern): Pattern {
        return Pattern(
            id = ravelryPattern.id,
            name = ravelryPattern.name,
            author = ravelryPattern.patternAuthor.name,
            imageUrl = ravelryPattern.firstPhoto?.small2Url ?: ""
        )
    }
}
