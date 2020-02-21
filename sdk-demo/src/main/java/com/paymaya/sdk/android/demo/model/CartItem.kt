package com.paymaya.sdk.android.demo.model

import com.paymaya.sdk.android.checkout.models.ItemAmount
import java.math.BigDecimal

data class CartItem(
    var quantity: Int,
    var totalAmount: BigDecimal,
    val name: String,
    val currency: String,
    val amount: ItemAmount,
    val description: String? = null,
    val code: String? = null
)