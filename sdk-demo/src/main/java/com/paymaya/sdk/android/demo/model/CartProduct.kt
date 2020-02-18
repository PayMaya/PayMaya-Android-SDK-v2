package com.paymaya.sdk.android.demo.model

import java.math.BigDecimal

data class CartProduct(
    val name: String,
    val items: List<ShopProduct>,
    val totalAmount: BigDecimal,
    val code: String? = null,
    val description: String? = null
)