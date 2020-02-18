package com.paymaya.sdk.android.demo.model

data class ShopProduct(
    val name: String,
    val code: String? = null,
    val description: String? = null,
    val amount: ProductDetail.ItemAmount? = null,
    val currency: String
)