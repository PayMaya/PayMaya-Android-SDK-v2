package com.paymaya.sdk.android.demo.ui.cart

import com.paymaya.sdk.android.demo.mapper.CheckoutPaymentMapper
import com.paymaya.sdk.android.demo.mapper.SinglePaymentsMapper
import com.paymaya.sdk.android.demo.mapper.WalletLinkMapper
import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.usecase.FetchProductsFromCartUseCase
import com.paymaya.sdk.android.demo.usecase.RemoveProductFromCartUseCase
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import java.math.BigDecimal

class CartPresenter(
    private val fetchProductsFromCartUseCase: FetchProductsFromCartUseCase,
    private val removeProductFromCartUseCase: RemoveProductFromCartUseCase,
    private val checkoutPaymentMapper: CheckoutPaymentMapper,
    private val singlePaymentsMapper: SinglePaymentsMapper,
    private val payMayaWalletLinkMapper: WalletLinkMapper
) : CartContract.Presenter {

    private var view: CartContract.View? = null

    override fun viewCreated(view: CartContract.View) {
        this.view = view
        view.clearBadgeCounter()
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
        val checkoutPaymentModel = checkoutPaymentMapper.run()
        checkoutPaymentModel?.let { view?.payWithCheckout(it) }
    }

    override fun payWithPayMayaClicked() {
        val singlePaymentRequest: SinglePaymentRequest? = singlePaymentsMapper.run()
        singlePaymentRequest?.let { view?.payWithPayMaya(it) }
    }

    override fun createWalletLinkClicked() {
        val walletLinkRequest: CreateWalletLinkRequest = payMayaWalletLinkMapper.run()
        view?.createWalletLink(walletLinkRequest)
    }
}