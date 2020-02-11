package com.paymaya.sdk.android.common.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.serialization.BigDecimalSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.*
import java.math.BigDecimal

@Parcelize
@Serializable
data class TotalAmount(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal,
    val currency: String,
    val details: AmountDetails? = null
) : Parcelable
