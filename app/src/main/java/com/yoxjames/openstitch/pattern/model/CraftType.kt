package com.yoxjames.openstitch.pattern.model

import com.yoxjames.openstitch.pattern.api.models.RavelryCraft

enum class CraftType {
    KNITTING, CROCHET;
}

sealed interface RavelryCraftException {
    object NullCraft : RavelryCraftException
    @JvmInline
    value class InvalidCraft(val ravelryCraft: RavelryCraft) : RavelryCraftException
}
