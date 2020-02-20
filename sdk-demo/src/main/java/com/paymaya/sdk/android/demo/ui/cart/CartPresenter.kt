package com.paymaya.sdk.android.demo.ui.cart

import com.paymaya.sdk.android.checkout.PayMayaCheckoutResult
import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.usecase.*
import com.paymaya.sdk.android.paywithpaymaya.CreateWalletLinkResult
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.SinglePaymentResult
import com.paymaya.sdk.android.vault.PayMayaVaultResult
import java.math.BigDecimal

class CartPresenter(
    private val fetchProductsFromCartUseCase: FetchProductsFromCartUseCase,
    private val removeProductFromCartUseCase: RemoveProductFromCartUseCase,
    private val createCheckoutRequestUseCase: CreateCheckoutRequestUseCase,
    private val createSinglePaymentsRequestUseCase: CreateSinglePaymentsRequestUseCase,
    private val createWalletLinkRequestUseCase: CreateWalletLinkRequestUseCase
) : CartContract.Presenter {

    private var view: CartContract.View? = null

    override fun viewCreated(view: CartContract.View) {
        this.view = view
        view.populateView(fetchProductsFromCartUseCase.run())
        view.setTotalAmount(getTotalAmount())
    }

    private fun getTotalAmount(): BigDecimal {
        var totalAmount = BigDecimal(0)
        fetchProductsFromCartUseCase.run().forEach {
            totalAmount += it.totalAmount
        }
        return totalAmount
    }

    override fun removeFromCartClicked(product: CartProduct) {
        removeProductFromCartUseCase.run(product)
        view?.populateView(fetchProductsFromCartUseCase.run())
        view?.setTotalAmount(getTotalAmount())
    }

    override fun payWithCheckoutClicked() {
        val checkoutRequest = createCheckoutRequestUseCase.run()
        checkoutRequest?.let { view?.payWithCheckout(it) }
    }

    override fun payWithPayMayaClicked() {
        val singlePaymentRequest = createSinglePaymentsRequestUseCase.run()
        singlePaymentRequest?.let { view?.payWithPayMaya(it) }
    }

    override fun createWalletLinkClicked() {
        val walletLinkRequest = createWalletLinkRequestUseCase.run()
        view?.createWalletLink(walletLinkRequest)
    }

    override fun payMayaVaultTokenizeCardClicked() {
        view?.payMayaVaultTokenizeCard()
    }

    override fun onCheckoutResult(checkoutResult: PayMayaCheckoutResult) {
        processCheckoutResult(checkoutResult)
    }

    override fun onPayWithPayMayaResult(payWithPayMayaResult: PayWithPayMayaResult) {
        processPayWithWithPayMayaResult(payWithPayMayaResult)
    }

    override fun onVaultResult(vaultResult: PayMayaVaultResult) {
        processVaultResult(vaultResult)
    }

    private fun processCheckoutResult(result: PayMayaCheckoutResult) {
        when (result) {
            is PayMayaCheckoutResult.Success -> {
                val message = "Success, checkoutId: ${result.checkoutId}"
                view?.showResultSuccessMessage(message)
            }

            is PayMayaCheckoutResult.Cancel -> {
                val message = "Canceled, checkoutId: ${result.checkoutId}"
                view?.showResultCancelMessage(message)
            }

            is PayMayaCheckoutResult.Failure -> {
                val message = "Failure, checkoutId: ${result.checkoutId}, exception: ${result.exception}"
                view?.showResultFailureMessage(message, result.exception)
            }
        }
    }

    private fun processPayWithWithPayMayaResult(result: PayWithPayMayaResult) {
        when (result) {
            is SinglePaymentResult.Success -> {
                val message = "Success, paymentId: ${result.paymentId}"
                view?.showResultSuccessMessage(message)
            }

            is SinglePaymentResult.Cancel -> {
                val message = "Canceled, paymentId: ${result.paymentId}"
                view?.showResultCancelMessage(message)
            }

            is SinglePaymentResult.Failure -> {
                val message =
                    "Failure, paymentId: ${result.paymentId}, exception: ${result.exception}"
                view?.showResultFailureMessage(message, result.exception)
            }

            is CreateWalletLinkResult.Success -> {
                val message = "Success, linkId: ${result.linkId}"
                view?.showResultSuccessMessage(message)
            }

            is CreateWalletLinkResult.Cancel -> {
                val message = "Canceled, linkId: ${result.linkId}"
                view?.showResultCancelMessage(message)
            }

            is CreateWalletLinkResult.Failure -> {
                val message =
                    "Failure, linkId: ${result.linkId}, exception: ${result.exception}"
                view?.showResultFailureMessage(message, result.exception)
            }
        }
    }

    private fun processVaultResult(result: PayMayaVaultResult) {
        when (result) {
            is PayMayaVaultResult.Success -> {
                val message = "Success, result: ${result.paymentTokenId}, ${result.state}"
                view?.showResultSuccessMessage(message)
            }

            is PayMayaVaultResult.Cancel -> {
                val message = "Canceled"
                view?.showResultCancelMessage(message)
            }
        }
    }
}