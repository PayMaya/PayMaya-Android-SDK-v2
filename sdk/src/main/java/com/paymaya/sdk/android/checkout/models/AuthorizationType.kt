package com.paymaya.sdk.android.checkout.models

import kotlinx.serialization.Serializable

@Serializable
public enum class AuthorizationType(val value: String) {

    Normal("NORMAL"),
    Final("FINAL"),
    Preauthorization("PREAUTHORIZATION")

}