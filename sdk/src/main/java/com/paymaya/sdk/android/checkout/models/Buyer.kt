package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Buyer model containing all basic information about the entity that bought items from the merchant's shop.
 *
 * @property firstName Buyer's first name.
 * @property middleName Buyer's middle name.
 * @property lastName Buyer's last name.
 * @property contact Model of buyer's contact. Contains all information about the contact.
 * @property shippingAddress Model of buyer's shipping address. Contains all information about buyer shipping address.
 * @property billingAddress Model of buyer's billing address. Contains all information about buyer billing address.
 * @property ipAddress Buyer's IP Address.
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
