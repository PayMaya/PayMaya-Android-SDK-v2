package com.paymaya.sdk.android.checkout.models

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutError(
    val code: String,
    val message: String,
    val parameters: List<CheckoutErrorParameter>? = null,
    val details: CheckoutErrorDetails? = null
) : PayMayaError() {

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
