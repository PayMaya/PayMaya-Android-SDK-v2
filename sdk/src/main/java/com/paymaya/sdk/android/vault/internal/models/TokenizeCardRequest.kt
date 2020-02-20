package com.paymaya.sdk.android.vault.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal data class TokenizeCardRequest(
    val card: Card
)
