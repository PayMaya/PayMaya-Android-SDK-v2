package com.paymaya.sdk.android.checkout.internal

import com.paymaya.sdk.android.checkout.models.Checkout

internal interface PayMayaCheckoutContract {
    interface View {
        fun finishFailure(checkoutId: String?, exception: Exception)
        fun loadUrl(redirectUrl: String)
        fun finishCanceled(checkoutId: String?)
        fun finishSuccess(checkoutId: String)
    }

    interface Presenter {
        fun viewCreated(view: View, checkoutModel: Checkout)
        fun viewDestroyed()
        fun backButtonPressed()
        fun urlBeingLoaded(url: String): Boolean
    }
}