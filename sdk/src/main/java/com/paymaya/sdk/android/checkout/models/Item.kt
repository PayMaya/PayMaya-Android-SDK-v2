package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

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
