package com.paymaya.sdk.android.paywithpaymaya.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateWalletLinkResponse(
    val linkId: String,
    val redirectUrl: String
)
