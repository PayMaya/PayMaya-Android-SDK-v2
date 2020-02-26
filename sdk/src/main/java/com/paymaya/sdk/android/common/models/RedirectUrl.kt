package com.paymaya.sdk.android.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Redirect URL model defining redirect URL's used to redirect to specific pages once the payment process is done.
 *
 * @property success Success url.
 * @property failure Failure url.
 * @property cancel Cancel url.
 */
@Parcelize
@Serializable
data class RedirectUrl(
    val success: String,
    val failure: String,
    val cancel: String
) : Parcelable
