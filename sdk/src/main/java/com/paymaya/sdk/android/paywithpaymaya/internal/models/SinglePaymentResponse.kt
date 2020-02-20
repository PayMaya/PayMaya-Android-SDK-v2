package com.paymaya.sdk.android.paywithpaymaya.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal data class SinglePaymentResponse(
    val paymentId: String,
    val redirectUrl: String
)
