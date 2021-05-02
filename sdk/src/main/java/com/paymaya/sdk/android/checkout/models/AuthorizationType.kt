package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
enum class AuthorizationType {
    NORMAL,
    FINAL,
    PREAUTHORIZATION
}
