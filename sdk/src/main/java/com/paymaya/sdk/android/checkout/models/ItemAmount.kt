package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.BigDecimalSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Parcelize
@Serializable
data class ItemAmount(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal,
    val details: AmountDetails? = null
) : Parcelable
