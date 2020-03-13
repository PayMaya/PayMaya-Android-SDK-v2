package com.paymaya.sdk.android.demo.ui.cart

import com.paymaya.sdk.android.checkout.PayMayaCheckout
import com.paymaya.sdk.android.checkout.PayMayaCheckoutResult
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import com.paymaya.sdk.android.vault.PayMayaVaultResult
import java.math.BigDecimal

interface CartContract {

    interface View {
        fun populateView(productsList: List<Item>)
        fun setTotalAmount(totalAmount: BigDecimal, currency: String)

        fun payWithCheckout(checkoutRequest: CheckoutRequest)
        fun payWithSinglePayment(singlePaymentRequest: SinglePaymentRequest)
        fun createWalletLink(walletLinkRequest: CreateWalletLinkRequest)
        fun payMayaVaultTokenizeCard()

        fun showResultSuccessMessage(message: String)
        fun showResultCancelMessage(message: String)
        fun showResultFailureMessage(message: String, exception: Exception)
        fun showPaymentIdNotAvailableMessage()
        fun showPaymentDetailedStatus(message: String)

        fun showProgressBar()
        fun hideProgressBar()
    }

    interface Presenter {
        fun viewCreated(
            view: View,
            payMayaCheckoutClient: PayMayaCheckout,
            payWithPayMayaClient: PayWithPayMaya
        )
        fun viewDestroyed()

        fun removeFromCartButtonClicked(product: Item)
        fun payWithCheckoutButtonClicked()
        fun payWithSinglePaymentButtonClicked()
        fun createWalletLinkButtonClicked()
        fun payMayaVaultButtonClicked()
        fun payMayaCheckRecentPaymentStatusClicked()

        fun checkoutCompleted(result: PayMayaCheckoutResult)
        fun payWithPayMayaCompleted(result: PayWithPayMayaResult)
        fun vaultCompleted(result: PayMayaVaultResult)
    }
}