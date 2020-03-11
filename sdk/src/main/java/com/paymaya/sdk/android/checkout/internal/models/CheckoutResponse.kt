package com.paymaya.sdk.android.checkout.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal data class CheckoutResponse(
    val checkoutId: String,
    val redirectUrl: String
)
