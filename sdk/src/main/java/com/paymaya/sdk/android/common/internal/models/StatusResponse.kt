package com.paymaya.sdk.android.common.internal.models

import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(
    val id: String,
    val status: PaymentStatus
)

enum class PaymentStatus {

    PENDING_TOKEN,

    //  Initial payment status of the checkout transaction
    PENDING_PAYMENT,

    // When payment transaction is not executed and expiration time has been reached.
    PAYMENT_EXPIRED,

    // When payment transaction is waiting for authentication.
    FOR_AUTHENTICATION,

    // When payment transaction is currently authenticating.
    AUTHENTICATING,

    // When authentication has been successfully executed. Authentication may be in the form of 3DS authentication for card transactions.
    AUTH_SUCCESS,

    // When authentication of payment has failed.
    AUTH_FAILED,

    // When payment is processing.
    PAYMENT_PROCESSING,

    // When payment is successfully processed.
    PAYMENT_SUCCESS,

    // When payment is not successfully processed.
    PAYMENT_FAILED,

    // When a successfully processed payment has been reversed (usually before a settlement cut-off for card-based payments).
    VOIDED,

    // When a successfully processed payment has been fully or partially reversed (usually after a settlement cut-off for card-based payments).
    REFUNDED
}
