package com.yoxjames.openstitch.pattern.model

import java.math.BigDecimal

sealed interface Price

object Free : Price

data class MonetaryPrice(
    val price: BigDecimal,
    val currencySymbol: String,
) : Price
