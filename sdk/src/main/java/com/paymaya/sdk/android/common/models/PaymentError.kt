package com.paymaya.sdk.android.common.models

import kotlinx.serialization.Serializable

/**
 * Payment error returned by PayMaya gateway.
 *
 * @property code Code.
 * @property message Message.
 * @property parameters Error parameters.
 * @property details Error details.
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
