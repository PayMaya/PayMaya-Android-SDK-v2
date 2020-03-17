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
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents the entity that bought items from the merchant's shop.
 * When provided must have at least one populated field.
 *
 * @property firstName First name of buyer.
 * @property middleName Middle name of buyer.
 * @property lastName Last name of buyer.
 * @property contact Contact details of buyer.
 * @property shippingAddress Shipping address of buyer.
 * @property billingAddress Billing address of buyer.
 * @property ipAddress IPv4 address of the buyer who issued the cart checkout (deprecated).
 */
@Parcelize
@Serializable
data class Buyer(
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val contact: Contact? = null,
    val shippingAddress: Address? = null,
    val billingAddress: Address? = null,
    @Deprecated(message = "Deprecated")
    val ipAddress: String? = null
) : Parcelable
