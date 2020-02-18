package com.paymaya.sdk.android.demo.ui.cart

import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import java.math.BigDecimal

interface CartContract {

    interface View {
        fun populateView(productsList: List<CartProduct>)
        fun setTotalAmount(totalAmount: BigDecimal)
        fun clearBadgeCounter()
        fun payWithCheckout(checkoutRequest: CheckoutRequest)
        fun payWithPayMaya(singlePaymentRequest: SinglePaymentRequest)
        fun createWalletLink(walletLinkRequest: CreateWalletLinkRequest)
    }

    interface Presenter {
        fun viewCreated(view: View)
        fun removeFromCartClicked(product: CartProduct)
        fun payWithCheckoutClicked()
        fun payWithPayMayaClicked()
        fun createWalletLinkClicked()
    }
}