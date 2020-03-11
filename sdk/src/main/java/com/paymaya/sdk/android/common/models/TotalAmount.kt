package com.paymaya.sdk.android.common.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.serialization.BigDecimalSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * Contains information about transaction amount: value, currency, included fees.
 *
 * @property value Payment amount to be charged.
 * @property currency Currency
 * @property details Breakdown of fees
 */
@Parcelize
@Serializable
data class TotalAmount(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal,
    val currency: String,
    val details: AmountDetails? = null
) : Parcelable
