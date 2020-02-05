package com.paymaya.sdk.android.checkout.models

import kotlinx.serialization.Serializable

@Serializable
data class GenericError(
    val code: String,
    val error: String,
    val reference: String
) : PayMayaError() {

    override fun toString(): String =
        "Error code: $code, message: $error, reference: $reference"
}
