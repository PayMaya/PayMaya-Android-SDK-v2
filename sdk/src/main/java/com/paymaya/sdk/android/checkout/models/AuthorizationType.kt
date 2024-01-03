package com.paymaya.sdk.android.checkout.models

import kotlinx.serialization.Serializable

@Serializable
enum class AuthorizationType {
    NORMAL,
    FINAL,
    PREAUTHORIZATION
}
