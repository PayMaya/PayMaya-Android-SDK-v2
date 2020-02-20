package com.paymaya.sdk.android.demo.ui.cart

import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.usecase.*
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
}