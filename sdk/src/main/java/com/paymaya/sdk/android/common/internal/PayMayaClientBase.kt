package com.paymaya.sdk.android.common.internal

import com.paymaya.sdk.android.common.CheckPaymentStatusResult
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import kotlinx.coroutines.runBlocking

internal abstract class PayMayaClientBase internal constructor(
    protected val clientPublicKey: String,
    protected val environment: PayMayaEnvironment,
    protected val logLevel: LogLevel,
    private val checkStatusUseCase: CheckStatusUseCase
) {

    fun checkPaymentStatus(id: String): CheckPaymentStatusResult =
        runBlocking {
            val result = checkStatusUseCase.run(id)
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
