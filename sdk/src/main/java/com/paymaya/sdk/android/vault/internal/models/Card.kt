package com.paymaya.sdk.android.vault.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal data class Card(
    val number: String,
    val expMonth: String,
    val expYear: String,
    val cvc: String
)
