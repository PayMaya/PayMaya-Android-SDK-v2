package com.paymaya.sdk.android.checkout.models

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutErrorParameter(
    val field: String? = null,
    val description: String? = null
) : java.io.Serializable {

    override fun toString(): String =
        "$field: $description"
}
