package com.paymaya.sdk.android.common

import com.paymaya.sdk.android.common.internal.CheckStatusUseCase
import com.paymaya.sdk.android.common.internal.ErrorResponseWrapper
import com.paymaya.sdk.android.common.internal.ResponseWrapper
import com.paymaya.sdk.android.common.internal.StatusSuccessResponseWrapper
import kotlinx.coroutines.runBlocking

abstract class PayMayaClientBase internal constructor(
    protected val clientKey: String,
    protected val environment: PayMayaEnvironment,
    protected val logLevel: LogLevel,
    private val checkStatusUseCase: CheckStatusUseCase
) {

    fun checkStatus(checkoutId: String): CheckPaymentStatusResult =
        runBlocking {
            val result = checkStatusUseCase.run(checkoutId)
            processResponse(result)
        }

    private fun processResponse(responseWrapper: ResponseWrapper): CheckPaymentStatusResult =
        when (responseWrapper) {
            is StatusSuccessResponseWrapper -> processSuccessResponse(responseWrapper)
            is ErrorResponseWrapper -> processErrorResponse(responseWrapper)
            else -> throw IllegalStateException(responseWrapper.toString())
        }

    private fun processSuccessResponse(statusSuccessResponseWrapper: StatusSuccessResponseWrapper):
            CheckPaymentStatusResult =
        CheckPaymentStatusResult.Success(
            status = statusSuccessResponseWrapper.status
        )

    private fun processErrorResponse(responseWrapper: ErrorResponseWrapper): CheckPaymentStatusResult {
        val exception = responseWrapper.exception
        if (exception is kotlinx.coroutines.CancellationException) {
            return CheckPaymentStatusResult.Cancel
        }

        return CheckPaymentStatusResult.Failure(exception)
    }
}
