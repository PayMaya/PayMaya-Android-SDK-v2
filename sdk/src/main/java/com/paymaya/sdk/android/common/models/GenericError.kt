package com.paymaya.sdk.android.common.models

import kotlinx.serialization.Serializable

/**
 * Generic error returned by the PayMaya gateway.
 *
 * @property code Code.
 * @property error Message.
 * @property reference Reference.
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
