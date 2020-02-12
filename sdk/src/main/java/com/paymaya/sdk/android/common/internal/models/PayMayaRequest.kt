package com.paymaya.sdk.android.common.internal.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.models.RedirectUrl

interface PayMayaRequest : Parcelable {
    val redirectUrl: RedirectUrl
}
