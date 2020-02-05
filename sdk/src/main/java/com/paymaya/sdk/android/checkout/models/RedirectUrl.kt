package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RedirectUrl(
    val success: String,
    val failure: String,
    val cancel: String
) : Parcelable
