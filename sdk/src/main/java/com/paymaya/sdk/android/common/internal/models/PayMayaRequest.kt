package com.paymaya.sdk.android.common.internal.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.models.RedirectUrl

internal interface PayMayaRequest : Parcelable {
    val redirectUrl: RedirectUrl
}
