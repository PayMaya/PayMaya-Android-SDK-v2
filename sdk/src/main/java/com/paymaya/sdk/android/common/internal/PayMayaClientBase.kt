/*
 * Copyright (c) 2020  PayMaya Philippines, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
