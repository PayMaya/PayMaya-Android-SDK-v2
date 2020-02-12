package com.paymaya.sdk.android.paywithpaymaya.models

import com.paymaya.sdk.android.common.internal.models.PayMayaRequest
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.serialization.JSONObjectParceler
import com.paymaya.sdk.android.common.serialization.JSONObjectSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith
import kotlinx.serialization.Serializable
import org.json.JSONObject

@Parcelize
@Serializable
data class CreateWalletLinkRequest(
    val requestReferenceNumber: String,
    override val redirectUrl: RedirectUrl,
    // TODO makes sense to have this? maybe metadata should be a string?
    @Serializable(with = JSONObjectSerializer::class)
    val metadata: @WriteWith<JSONObjectParceler> JSONObject? = null
) : PayMayaRequest
