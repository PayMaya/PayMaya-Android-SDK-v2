package com.paymaya.sdk.android.demo.model

import java.math.BigDecimal

data class ShopItem(
    val name: String,
    val currency: String,
    val value: BigDecimal,
    val code: String? = null,
    val description: String? = null,
    val discount: BigDecimal? = null
)