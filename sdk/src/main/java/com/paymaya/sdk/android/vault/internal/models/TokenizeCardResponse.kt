package com.paymaya.sdk.android.vault.internal.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
internal class TokenizeCardResponse(
    val paymentTokenId: String,
    val state: String,
    val createdAt: String,
    val updatedAt: String,
    val issuer: String
) : Parcelable
