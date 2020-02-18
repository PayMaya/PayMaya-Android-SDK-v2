package com.paymaya.sdk.android.common.internal.screen

import com.paymaya.sdk.android.common.exceptions.PaymentFailedException
import com.paymaya.sdk.android.common.internal.ErrorResponse
import com.paymaya.sdk.android.common.internal.ResponseWrapper
import com.paymaya.sdk.android.common.internal.SendRequestBaseUseCase
import com.paymaya.sdk.android.common.internal.SuccessResponse
import com.paymaya.sdk.android.common.internal.models.PayMayaRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class PayMayaPaymentPresenter<R : PayMayaRequest, U : SendRequestBaseUseCase<R>>(
    private val sendRequestUseCase: U
) : PayMayaPaymentContract.Presenter<R>, CoroutineScope {

    private val job: Job = Job()

    private lateinit var requestModel: R
    private var view: PayMayaPaymentContract.View? = null
    private var resultId: String? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun viewCreated(view: PayMayaPaymentContract.View, request: R) {
        this.view = view
        this.requestModel = request

        startPayment()
    }

    override fun viewDestroyed() {
        job.cancel()
        this.view = null
    }

    override fun backButtonPressed() {
        job.cancel()
        view?.finishCanceled(resultId)
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
            val responseWrapper = sendRequest()
            processResponse(responseWrapper)
        }

    private suspend fun sendRequest(): ResponseWrapper {
        resultId = null
        return sendRequestUseCase.run(requestModel)
    }

    private fun processResponse(responseWrapper: ResponseWrapper) {
        when (responseWrapper) {
            is SuccessResponse -> processSuccessResponse(responseWrapper)
            is ErrorResponse -> processErrorResponse(responseWrapper)
        }
    }

    private fun processSuccessResponse(responseWrapper: SuccessResponse) {
        val redirectUrl = responseWrapper.redirectUrl
        resultId = responseWrapper.responseId
        view?.loadUrl(redirectUrl)
    }

    private fun processErrorResponse(responseWrapper: ErrorResponse) {
        view?.finishFailure(resultId, responseWrapper.exception)
    }
}