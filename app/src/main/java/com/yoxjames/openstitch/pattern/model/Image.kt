package com.yoxjames.openstitch.pattern.model

import arrow.core.Option

data class Image(
    val imageUrl: String,
    val caption: Option<String>
)
