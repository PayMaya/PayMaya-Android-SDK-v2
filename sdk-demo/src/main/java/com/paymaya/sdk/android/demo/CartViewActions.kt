package com.paymaya.sdk.android.demo

import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest

interface CartViewActions {
    fun updateBadgeCounter(value: Int)
    fun removeBadgeCounter()
    fun payWithCheckout(checkoutRequest: CheckoutRequest)
    fun payWithPayMayaSinglePayment(singlePaymentRequest: SinglePaymentRequest)
    fun payWithPayMayaCreateWalletLink(createWalletLinkRequest: CreateWalletLinkRequest)
}