package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.serialization.BigDecimalSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * Details about each item's amount.
 *
 * @property value Payment amount to be charged to the card.
 * @property details Breakdown of fees.
 */
@Parcelize
@Serializable
data class ItemAmount(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal,
    val details: AmountDetails? = null
) : Parcelable
