package com.paymaya.sdk.android.common.models

import kotlinx.serialization.Serializable

/**
 * PaymentErrorParameter model defining information about single payment parameter and combine
 * information about his field and description.
 *
 * @property field Payment parameter field.
 * @property description Payment parameter description.
 */
@Serializable
data class PaymentErrorParameter(
    val field: String? = null,
    val description: String? = null
) : java.io.Serializable {

    override fun toString(): String =
        "$field: $description"
}
