package com.paymaya.sdk.android.demo

import com.paymaya.sdk.android.checkout.models.Checkout

interface CartViewActions {
    fun updateBadgeCounter(value: Int)
    fun payWithCheckout(checkout: Checkout)
}