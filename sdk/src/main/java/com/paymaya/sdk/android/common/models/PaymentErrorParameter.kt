package com.paymaya.sdk.android.common.models

import kotlinx.serialization.Serializable

/**
 * Payment error parameter.
 *
 * @property field Field name.
 * @property description Error description.
 */
@Serializable
data class PaymentErrorParameter(
    val field: String? = null,
    val description: String? = null
) : java.io.Serializable {

    override fun toString(): String =
        "$field: $description"
}
