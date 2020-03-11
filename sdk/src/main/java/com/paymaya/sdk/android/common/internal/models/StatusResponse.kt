package com.paymaya.sdk.android.common.internal.models

import com.paymaya.sdk.android.common.PaymentStatus
import kotlinx.serialization.Serializable

@Serializable
internal data class StatusResponse(
    val id: String,
    val status: PaymentStatus
)
