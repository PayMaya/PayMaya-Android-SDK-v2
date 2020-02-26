package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Contact model. Represents buyer's contact details. If the email field is provided, a payment receipt
 * will be sent to the email address.
 *
 * @property phone Buyer's phone number
 * @property email Buyer's e-mail address
 */
@Parcelize
@Serializable
data class Contact(
    val phone: String? = null,
    val email: String? = null
) : Parcelable
