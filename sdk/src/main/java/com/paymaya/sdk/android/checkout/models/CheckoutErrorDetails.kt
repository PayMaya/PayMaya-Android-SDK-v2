package com.paymaya.sdk.android.checkout.models

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutErrorDetails(
    val responseCode: String? = null,
    val responseDescription: String? = null,
    val requestReferenceNumber: String? = null
) : java.io.Serializable {

    override fun toString(): String =
        "Request reference number: $requestReferenceNumber\n" +
                "Response code: $responseCode\n" +
                "Response description: $responseDescription"
}
