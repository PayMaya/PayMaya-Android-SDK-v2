package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.BigDecimalSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Parcelize
@Serializable
class AmountDetails(
    @Serializable(with = BigDecimalSerializer::class)
    val discount: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val serviceCharge: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val shippingFee: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val tax: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val subtotal: BigDecimal? = null
) : Parcelable
