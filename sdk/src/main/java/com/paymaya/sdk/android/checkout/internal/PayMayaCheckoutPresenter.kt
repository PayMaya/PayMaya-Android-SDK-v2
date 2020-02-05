package com.paymaya.sdk.android.checkout.internal

import com.paymaya.sdk.android.checkout.exceptions.PaymentFailedException
import com.paymaya.sdk.android.checkout.models.Checkout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class PayMayaCheckoutPresenter(
    private val sendCheckoutRequestUseCase: SendCheckoutRequestUseCase
) : PayMayaCheckoutContract.Presenter, CoroutineScope {

    private val job: Job = Job()

    private lateinit var checkoutModel: Checkout
    private var view: PayMayaCheckoutContract.View? = null
    private var checkoutId: String? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun viewCreated(view: PayMayaCheckoutContract.View, checkoutModel: Checkout) {
        this.view = view
        this.checkoutModel = checkoutModel

        requestCreateCheckout()
    }

    override fun viewDestroyed() {
        job.cancel()
        this.view = null
    }

    override fun backButtonPressed() {
        job.cancel()
        view?.finishCanceled(checkoutId)
    }

    override fun urlBeingLoaded(url: String): Boolean {
        val redirectUrl = checkoutModel.redirectUrl
        when {
            url.startsWith(redirectUrl.success) -> view?.finishSuccess(checkoutId!!)
            url.startsWith(redirectUrl.cancel) -> view?.finishCanceled(checkoutId)
            url.startsWith(redirectUrl.failure) -> view?.finishFailure(checkoutId, PaymentFailedException)
            else -> return false
        }
        return true
    }

    private fun requestCreateCheckout() =
        launch {
            val responseWrapper = sendCheckoutRequest()
            processResponse(responseWrapper)
        }

    private suspend fun sendCheckoutRequest(): ResponseWrapper {
        checkoutId = null
        return sendCheckoutRequestUseCase.run(checkoutModel)
    }

    private fun processResponse(responseWrapper: ResponseWrapper) {
        when (responseWrapper) {
            is SuccessResponse -> processSuccessResponse(responseWrapper)
            is ErrorResponse -> processErrorResponse(responseWrapper)
        }
    }

    private fun processSuccessResponse(responseWrapper: SuccessResponse) {
        val redirectUrl = responseWrapper.redirectUrl
        checkoutId = responseWrapper.checkoutId
        view?.loadUrl(redirectUrl)
    }

    private fun processErrorResponse(responseWrapper: ErrorResponse) {
        view?.finishFailure(checkoutId, responseWrapper.exception)
    }

    companion object {
        private const val TAG = "PayMayaCheckoutPresenter"
    }
}