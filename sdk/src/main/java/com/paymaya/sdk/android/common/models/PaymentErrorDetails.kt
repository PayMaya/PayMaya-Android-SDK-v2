package com.paymaya.sdk.android.common.models

import kotlinx.serialization.Serializable

/**
 * Payment error details.
 *
 * @property responseCode Response code.
 * @property responseDescription Response description.
 * @property requestReferenceNumber Request reference number.
 */
@Serializable
data class PaymentErrorDetails(
    val responseCode: String? = null,
    val responseDescription: String? = null,
    val requestReferenceNumber: String? = null
) : java.io.Serializable {

    override fun toString(): String =
        "Request reference number: $requestReferenceNumber\n" +
                "Response code: $responseCode\n" +
                "Response description: $responseDescription"
}
