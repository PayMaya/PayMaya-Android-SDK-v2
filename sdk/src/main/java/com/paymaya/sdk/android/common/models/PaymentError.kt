package com.paymaya.sdk.android.common.models

import kotlinx.serialization.Serializable

/**
 * PaymentError class describes an exception that occurred during payment process.
 *
 * @property code The event code that are associated with the error.
 * @property message Error message.
 * @property parameters List of payment error parameters.
 * @property details Model of PaymentErrorDetail which collect all of payment error detail information.
 */
@Serializable
data class PaymentError(
    val code: String,
    val message: String,
    val parameters: List<PaymentErrorParameter>? = null,
    val details: PaymentErrorDetails? = null
) : BaseError() {

    override fun toString(): String {
        var result = "Error code: $code\nError message: $message"

        details?.let {
            result += "\n" + it.toString()
        }
        parameters?.forEach {
            result += "\n" + it.toString()
        }

        return result
    }
}
