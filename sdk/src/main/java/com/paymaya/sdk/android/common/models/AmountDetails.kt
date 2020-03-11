/*
 * Copyright (c) 2020  PayMaya Philippines, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
