package com.paymaya.sdk.android.demo.ui.cart

import com.paymaya.sdk.android.checkout.PayMayaCheckoutResult
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import com.paymaya.sdk.android.vault.PayMayaVaultResult
import java.lang.Exception
import java.math.BigDecimal

interface CartContract {

    interface View {
        fun populateView(productsList: List<CartProduct>)
        fun setTotalAmount(totalAmount: BigDecimal)
        fun payWithCheckout(checkoutRequest: CheckoutRequest)
        fun payWithPayMaya(singlePaymentRequest: SinglePaymentRequest)
        fun createWalletLink(walletLinkRequest: CreateWalletLinkRequest)
        fun payMayaVaultTokenizeCard()
        fun showResultSuccessMessage(message: String)
        fun showResultCancelMessage(message: String)
        fun showResultFailureMessage(message: String, exception: Exception)
    }

    interface Presenter {
        fun viewCreated(view: View)
        fun removeFromCartClicked(product: CartProduct)
        fun payWithCheckoutClicked()
        fun payWithPayMayaClicked()
        fun createWalletLinkClicked()
        fun payMayaVaultTokenizeCardClicked()
        fun onCheckoutResult(checkoutResult: PayMayaCheckoutResult?)
        fun onPayWithPayMayaResult(payWithPayMayaResult: PayWithPayMayaResult?)
        fun onVaultResult(vaultResult: PayMayaVaultResult?)
    }
}