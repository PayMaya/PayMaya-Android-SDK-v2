package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.JSONObjectParceler
import com.paymaya.sdk.android.common.JSONObjectSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith
import kotlinx.serialization.Serializable
import org.json.JSONObject

@Parcelize
@Serializable
data class Checkout(
    val totalAmount: TotalAmount,
    val buyer: Buyer? = null,
    val items: List<Item>,
    val requestReferenceNumber: String,
    val redirectUrl: RedirectUrl,
    // TODO makes sense to have this? maybe metadata should be a string?
    @Serializable(with = JSONObjectSerializer::class)
    val metadata: @WriteWith<JSONObjectParceler> JSONObject? = null
) : Parcelable
