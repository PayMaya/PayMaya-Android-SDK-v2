package com.paymaya.sdk.android.common.models

import kotlinx.serialization.Serializable

@Serializable
data class GenericError(
    val code: String,
    val error: String,
    val reference: String
) : BaseError() {

    override fun toString(): String =
        "Error code: $code, message: $error, reference: $reference"
}
