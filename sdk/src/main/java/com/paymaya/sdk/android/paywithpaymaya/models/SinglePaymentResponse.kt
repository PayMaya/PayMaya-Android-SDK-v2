package com.paymaya.sdk.android.paywithpaymaya.models

import kotlinx.serialization.Serializable

@Serializable
data class SinglePaymentResponse(
    val paymentId: String,
    val redirectUrl: String
)
