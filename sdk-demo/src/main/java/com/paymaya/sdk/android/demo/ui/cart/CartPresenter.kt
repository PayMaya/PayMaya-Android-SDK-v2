package com.paymaya.sdk.android.demo.ui.cart

import com.paymaya.sdk.android.checkout.PayMayaCheckout
import com.paymaya.sdk.android.checkout.PayMayaCheckoutResult
import com.paymaya.sdk.android.checkout.models.Buyer
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.common.CheckPaymentStatusResult
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.Constants
import com.paymaya.sdk.android.demo.data.CartRepository
import com.paymaya.sdk.android.paywithpaymaya.CreateWalletLinkResult
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.SinglePaymentResult
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import com.paymaya.sdk.android.vault.PayMayaVaultResult
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CartPresenter(
    private val cartRepository: CartRepository
) : CartContract.Presenter, CoroutineScope {

    private lateinit var payMayaCheckoutClient: PayMayaCheckout
    private lateinit var payWithPayMayaClient: PayWithPayMaya
    private var view: CartContract.View? = null
    private var resultId: String? = null
    private var paymentMethod: PaymentMethod? = null
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun viewCreated(
        view: CartContract.View,
        payMayaCheckoutClient: PayMayaCheckout,
        payWithPayMayaClient: PayWithPayMaya
    ) {
        this.view = view
        this.payMayaCheckoutClient = payMayaCheckoutClient
        this.payWithPayMayaClient = payWithPayMayaClient
        updateProductsList()
    }

    override fun viewDestroyed() {
        this.view = null
        job.cancel()
    }

    private fun updateProductsList() {
        val products = cartRepository.getItems()
        val totalAmount = cartRepository.getTotalAmount()
        view?.populateView(products)
        view?.setTotalAmount(totalAmount, Constants.CURRENCY)
    }

    override fun removeFromCartButtonClicked(product: Item) {
        cartRepository.removeItem(product)
        updateProductsList()
    }

    override fun payWithCheckoutButtonClicked() {
        paymentMethod = PaymentMethod.CHECKOUT
        resultId = null
        if (cartRepository.getItems().isNotEmpty()) {
            val checkoutRequest = buildCheckoutRequest()
            view?.payWithCheckout(checkoutRequest)
        }
    }

    private fun buildCheckoutRequest() =
        CheckoutRequest(
            TotalAmount(
                cartRepository.getTotalAmount(),
                Constants.CURRENCY,
                AmountDetails()
            ),
            Buyer(
                firstName = "John",
                middleName = "Thomas",
                lastName = "Smith",
                contact = null,
                shippingAddress = null,
                billingAddress = null,
                ipAddress = null
            ),
            cartRepository.getItems(),
            getRequestReferenceNumber(),
            REDIRECT_URL
        )

    override fun payWithSinglePaymentButtonClicked() {
        paymentMethod = PaymentMethod.PAY_WITH_PAYMAYA_SINGLE_PAYMENT
        resultId = null
        if (cartRepository.getItems().isNotEmpty()) {
            val singlePaymentRequest = buildSinglePaymentRequest()
            view?.payWithSinglePayment(singlePaymentRequest)
        }
    }

    private fun buildSinglePaymentRequest() =
        SinglePaymentRequest(
            TotalAmount(
                cartRepository.getTotalAmount(),
                Constants.CURRENCY,
                AmountDetails()
            ),
            getRequestReferenceNumber(),
            REDIRECT_URL
        )

    override fun createWalletLinkButtonClicked() {
        paymentMethod = null
        resultId = null
        val walletLinkRequest = buildCreateWalletLinkRequest()
        view?.createWalletLink(walletLinkRequest)
    }

    private fun buildCreateWalletLinkRequest() =
        CreateWalletLinkRequest(
            getRequestReferenceNumber(),
            REDIRECT_URL
        )

    override fun payMayaVaultButtonClicked() {
        paymentMethod = null
        resultId = null
        view?.payMayaVaultTokenizeCard()
    }

    override fun payMayaCheckRecentPaymentStatusClicked() {
        if (paymentMethod == null) {
            // PayWithPayMaya/Wallet Link and Vault/Tokenize Card are not direct payments
            view?.showPaymentIdNotAvailableMessage()
            return
        }

        resultId?.let {
            launch {
                view?.showProgressBar()
                val result = checkPaymentStatus(it)
                view?.hideProgressBar()
                checkPaymentStatusCompleted(result)
            }
        } ?: view?.showPaymentIdNotAvailableMessage()
    }

    private suspend fun checkPaymentStatus(id: String) =
        withContext(Dispatchers.IO) {
            // NOTE: checkStatus() is blocking, don't run it on the Main thread.
            when (paymentMethod) {
                PaymentMethod.CHECKOUT -> payMayaCheckoutClient.checkPaymentStatus(id)
                PaymentMethod.PAY_WITH_PAYMAYA_SINGLE_PAYMENT -> payWithPayMayaClient.checkPaymentStatus(id)
                else -> throw IllegalStateException("Not supported method type")
            }
        }

    override fun checkoutCompleted(result: PayMayaCheckoutResult) {
        when (result) {
            is PayMayaCheckoutResult.Success -> {
                resultId = result.checkoutId
                val message = "Success, checkoutId: ${result.checkoutId}"
                view?.showResultSuccessMessage(message)
            }

            is PayMayaCheckoutResult.Cancel -> {
                resultId = result.checkoutId
                val message = "Canceled, checkoutId: ${result.checkoutId}"
                view?.showResultCancelMessage(message)
            }

            is PayMayaCheckoutResult.Failure -> {
                resultId = result.checkoutId
                val message = "Failure, checkoutId: ${result.checkoutId}, exception: ${result.exception}"
                view?.showResultFailureMessage(message, result.exception)
            }
        }
    }

    override fun payWithPayMayaCompleted(result: PayWithPayMayaResult) {
        when (result) {
            is SinglePaymentResult.Success -> {
                resultId = result.paymentId
                val message = "Success, paymentId: ${result.paymentId}"
                view?.showResultSuccessMessage(message)
            }

            is SinglePaymentResult.Cancel -> {
                resultId = result.paymentId
                val message = "Canceled, paymentId: ${result.paymentId}"
                view?.showResultCancelMessage(message)
            }

            is SinglePaymentResult.Failure -> {
                resultId = result.paymentId
                val message =
                    "Failure, paymentId: ${result.paymentId}, exception: ${result.exception}"
                view?.showResultFailureMessage(message, result.exception)
            }

            is CreateWalletLinkResult.Success -> {
                resultId = result.linkId
                val message = "Success, linkId: ${result.linkId}"
                view?.showResultSuccessMessage(message)
            }

            is CreateWalletLinkResult.Cancel -> {
                resultId = result.linkId
                val message = "Canceled, linkId: ${result.linkId}"
                view?.showResultCancelMessage(message)
            }

            is CreateWalletLinkResult.Failure -> {
                resultId = result.linkId
                val message =
                    "Failure, linkId: ${result.linkId}, exception: ${result.exception}"
                view?.showResultFailureMessage(message, result.exception)
            }
        }
    }

    override fun vaultCompleted(result: PayMayaVaultResult) {
        when (result) {
            is PayMayaVaultResult.Success -> {
                resultId = result.paymentTokenId
                val message = "Success, result: ${result.paymentTokenId}, ${result.state}"
                view?.showResultSuccessMessage(message)
            }

            is PayMayaVaultResult.Cancel -> {
                resultId = null
                val message = "Canceled"
                view?.showResultCancelMessage(message)
            }
        }
    }

    private fun checkPaymentStatusCompleted(result: CheckPaymentStatusResult) {
        when (result) {
            is CheckPaymentStatusResult.Success -> {
                val message = "Payment status: ${result.status}"
                view?.showPaymentDetailedStatus(message)
            }

            is CheckPaymentStatusResult.Cancel -> {
                val message = "Checking status canceled"
                view?.showResultCancelMessage(message)
            }

            is CheckPaymentStatusResult.Failure -> {
                val message = "Checking status failed, exception: ${result.exception}"
                view?.showResultFailureMessage(message, result.exception)
            }
        }
    }

    private fun getRequestReferenceNumber() =
        (++REQUEST_REFERENCE_NUMBER).toString()

    private enum class PaymentMethod {
        CHECKOUT,
        PAY_WITH_PAYMAYA_SINGLE_PAYMENT
    }

    companion object {
        private var REQUEST_REFERENCE_NUMBER = 0
        private val REDIRECT_URL = RedirectUrl(
            success = Constants.REDIRECT_URL_SUCCESS,
            failure = Constants.REDIRECT_URL_FAILURE,
            cancel = Constants.REDIRECT_URL_CANCEL
        )
    }
}