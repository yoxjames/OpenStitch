package com.yoxjames.openstitch.pattern.model

import java.math.BigDecimal

sealed interface Price

object Free : Price

object None : Price

@JvmInline
value class MonetaryPrice(
    val price: BigDecimal
) : Price
