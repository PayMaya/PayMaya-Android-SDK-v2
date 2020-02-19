package com.paymaya.sdk.android.demo.model

import com.paymaya.sdk.android.checkout.models.ItemAmount

data class ShopProduct(
    val name: String,
    val code: String? = null,
    val description: String? = null,
    val amount: ItemAmount,
    val currency: String
)