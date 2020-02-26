package com.paymaya.sdk.android.common.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.serialization.BigDecimalSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.*
import java.math.BigDecimal

/**
 * Total amount model. Contains information about amount total value, currency, and details for item's amount.
 *
 * @property value Amounts's value
 * @property currency Amounts's currency
 * @property details Amount details model containing all information about amount.
 */
@Parcelize
@Serializable
data class TotalAmount(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal,
    val currency: String,
    val details: AmountDetails? = null
) : Parcelable
