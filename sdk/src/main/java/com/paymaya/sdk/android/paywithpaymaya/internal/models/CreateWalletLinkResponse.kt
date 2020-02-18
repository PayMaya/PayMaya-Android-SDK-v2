package com.paymaya.sdk.android.paywithpaymaya.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateWalletLinkResponse(
    val linkId: String,
    val redirectUrl: String
)
