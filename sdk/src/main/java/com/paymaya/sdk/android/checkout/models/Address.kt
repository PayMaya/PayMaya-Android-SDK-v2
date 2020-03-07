package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Address of the buyer.
 *
 * @property line1 Address line 1
 * @property line2 Address line 2
 * @property city City
 * @property state State/Province
 * @property zipCode Zip code
 * @property countryCode ISO 3166-2 country code
 */
@Parcelize
@Serializable
data class Address(
    val line1: String? = null,
    val line2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val countryCode: String? = null
) : Parcelable
