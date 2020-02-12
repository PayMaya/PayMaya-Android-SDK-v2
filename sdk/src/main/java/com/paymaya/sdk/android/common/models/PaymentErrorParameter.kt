package com.paymaya.sdk.android.common.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentErrorParameter(
    val field: String? = null,
    val description: String? = null
) : java.io.Serializable {

    override fun toString(): String =
        "$field: $description"
}
