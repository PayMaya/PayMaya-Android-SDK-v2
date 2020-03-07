package com.paymaya.sdk.android.common.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.serialization.BigDecimalSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * Amount details.
 * In checkout process, the merchant can optionally provide these details.
 * If provided, it will reflect to the Checkout Page and will be viewed by the buyer for review.
 *
 * @property discount Discount.
 * @property serviceCharge Service charge.
 * @property shippingFee Shipping fee.
 * @property tax Tax.
 * @property subtotal Subtotal.
 */
@Parcelize
@Serializable
data class AmountDetails(
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
