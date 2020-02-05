package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

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
