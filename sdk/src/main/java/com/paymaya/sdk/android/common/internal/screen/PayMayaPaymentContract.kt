package com.paymaya.sdk.android.common.internal.screen

import com.paymaya.sdk.android.common.internal.models.PayMayaRequest

internal interface PayMayaPaymentContract {
    interface View {
        fun finishFailure(resultId: String?, exception: Exception)
        fun loadUrl(redirectUrl: String)
        fun finishCanceled(resultId: String?)
        fun finishSuccess(resultId: String)
    }

    interface Presenter<R : PayMayaRequest> {
        fun viewCreated(view: View, request: R)
        fun viewDestroyed()
        fun backButtonPressed()
        fun urlBeingLoaded(url: String): Boolean
    }
}
