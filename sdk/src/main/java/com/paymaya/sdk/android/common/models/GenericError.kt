package com.paymaya.sdk.android.common.models

import kotlinx.serialization.Serializable

/**
 * GenericError class defines generic exceptions.
 *
 * @property code The event code that are associated with the error.
 * @property error Error message.
 * @property reference Error reference.
 */
@Serializable
data class GenericError(
    val code: String,
    val error: String,
    val reference: String
) : BaseError() {

    override fun toString(): String =
        "Error code: $code, message: $error, reference: $reference"
}
