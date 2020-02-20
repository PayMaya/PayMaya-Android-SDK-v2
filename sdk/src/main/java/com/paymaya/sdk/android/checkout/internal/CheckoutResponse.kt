package com.paymaya.sdk.android.checkout.internal

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutResponse(
    val checkoutId: String,
    val redirectUrl: String
)
