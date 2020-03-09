package com.paymaya.sdk.android.common.internal.screen

import com.paymaya.sdk.android.common.PaymentStatus
import com.paymaya.sdk.android.common.exceptions.PaymentFailedException
import com.paymaya.sdk.android.common.internal.*
import com.paymaya.sdk.android.common.internal.models.PayMayaRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class PayMayaPaymentPresenter<R : PayMayaRequest, U : SendRequestBaseUseCase<R>>(
    private val sendRequestUseCase: U,
    private val checkStatusUseCase: CheckStatusUseCase,
    private val logger: Logger,
    private val paymentStatusAutoCheck: Boolean = true
) : PayMayaPaymentContract.Presenter<R>, CoroutineScope {

    private lateinit var requestModel: R
    private var view: PayMayaPaymentContract.View? = null
    private var resultId: String? = null
    private var paymentJob: Job? = null
    private var checkStatusJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun viewCreated(view: PayMayaPaymentContract.View, request: R) {
        this.view = view
        this.requestModel = request

        paymentJob = startPayment()
    }

    override fun viewDestroyed() {
        paymentJob?.cancel()
        checkStatusJob?.cancel()
        this.view = null
    }

    override fun backButtonPressed() {
        if (paymentStatusAutoCheck) {
            // On the first Back button click - stop loading page, send check payment status request
            // On the second Back button click - close activity with 'canceled' status
            checkStatusJob?.let {
                it.cancel()
                view?.finishCanceled(resultId)
            } ?: run {
                paymentJob?.cancel()
                handleCancellation()
            }
        } else {
            paymentJob?.cancel()
            view?.finishCanceled(resultId)
        }
    }

    override fun urlBeingLoaded(url: String): Boolean {
        val redirectUrl = requestModel.redirectUrl
        when {
            url.startsWith(redirectUrl.success) -> view?.finishSuccess(resultId!!)
            url.startsWith(redirectUrl.cancel) -> view?.finishCanceled(resultId)
            url.startsWith(redirectUrl.failure) -> view?.finishFailure(resultId, PaymentFailedException)
            else -> return false
        }
        return true
    }

    private fun startPayment() =
        launch {
            resultId = null
            val responseWrapper = sendRequestUseCase.run(requestModel)
            processResponse(responseWrapper)
        }

    private fun processResponse(responseWrapper: ResponseWrapper) {
        when (responseWrapper) {
            is RedirectSuccessResponseWrapper -> processSuccessResponse(responseWrapper)
            is ErrorResponseWrapper -> processErrorResponse(responseWrapper)
            is StatusSuccessResponseWrapper -> processStatusSuccessResponse(responseWrapper)
            else -> throw IllegalStateException("Unexpected response wrapper: ${responseWrapper.javaClass.simpleName}")
        }
    }

    private fun processSuccessResponse(redirectSuccessResponse: RedirectSuccessResponseWrapper) {
        val redirectUrl = redirectSuccessResponse.redirectUrl
        resultId = redirectSuccessResponse.resultId
        view?.loadUrl(redirectUrl)
    }

    private fun processErrorResponse(responseWrapper: ErrorResponseWrapper) {
        val exception = responseWrapper.exception
        if (exception is kotlinx.coroutines.CancellationException) {
            handleCancellation()
            return
        }

        view?.finishFailure(resultId, exception)
    }

    private fun processStatusSuccessResponse(statusSuccessResponseWrapper: StatusSuccessResponseWrapper) {
        val resultId = requireNotNull(resultId)

        logger.i(
            Constants.TAG,
            "Payment status: ${statusSuccessResponseWrapper.id}, ${statusSuccessResponseWrapper.status}"
        )

        when (statusSuccessResponseWrapper.status) {
            PaymentStatus.PAYMENT_SUCCESS -> view?.finishSuccess(resultId)
            PaymentStatus.AUTH_FAILED,
            PaymentStatus.PAYMENT_FAILED -> view?.finishFailure(resultId, PaymentFailedException)
            else -> view?.finishCanceled(resultId)
        }
    }

    private fun handleCancellation() {
        resultId?.let { id ->
            checkStatusJob = sendCheckStatusRequest(id)
        } ?: view?.finishCanceled(null)
    }

    private fun sendCheckStatusRequest(id: String) =
        launch {
            view?.hideWebView()
            view?.showProgressBar()
            view?.showCheckingPaymentStatusLabel()
            val responseWrapper = checkStatusUseCase.run(id)
            processResponse(responseWrapper)
        }
}
