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
    val ipAddress: String? = null
) : Parcelable
