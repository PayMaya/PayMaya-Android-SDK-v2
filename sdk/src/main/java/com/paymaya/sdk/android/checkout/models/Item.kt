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

package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.models.TotalAmount
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents the entity that can be purchased from a merchant's shop.
 *
 * @property name Item's product name.
 * @property quantity Item's quantity.
 * @property code Item's SKU code.
 * @property description Item's description.
 * @property amount Item's unit price.
 * @property totalAmount Item's total price.
 */
@Parcelize
@Serializable
data class Item(
    val name: String,
    val quantity: Int? = null,
    val code: String? = null,
    val description: String? = null,
    val amount: ItemAmount? = null,
    val totalAmount: TotalAmount
) : Parcelable
