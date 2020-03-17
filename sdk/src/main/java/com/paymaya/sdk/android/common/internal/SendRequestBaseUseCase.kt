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

import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.exceptions.HttpException
import com.paymaya.sdk.android.common.exceptions.InternalException
import com.paymaya.sdk.android.common.internal.Constants.TAG
import com.paymaya.sdk.android.common.models.GenericError
import com.paymaya.sdk.android.common.models.PaymentError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody
import java.net.HttpURLConnection

internal abstract class SendRequestBaseUseCase<T>(
    protected val json: Json,
    private val logger: Logger
) {

    suspend fun run(request: T): ResponseWrapper =
        withContext(Dispatchers.IO) {
            try {
                processResponse(
                    sendRequest(request)
                )
            } catch (e: Exception) {
                ErrorResponseWrapper(e)
            }
        }

    protected abstract suspend fun sendRequest(request: T): Response

    protected abstract fun prepareSuccessResponse(responseBody: ResponseBody): SuccessResponseWrapper

    private fun processResponse(httpResponse: Response): ResponseWrapper =
        when (httpResponse.code) {
            HttpURLConnection.HTTP_OK -> processHttpOkResponse(httpResponse)
            HttpURLConnection.HTTP_BAD_REQUEST -> processHttpBadRequestResponse(httpResponse)
            HttpURLConnection.HTTP_UNAUTHORIZED -> processHttpUnauthorizedResponse(httpResponse)
            else -> processOtherHttpErrorResponse(httpResponse)
        }

    private fun processHttpOkResponse(httpResponse: Response): ResponseWrapper =
        httpResponse.body?.let { responseBody ->
            try {
                prepareSuccessResponse(responseBody)
            } catch (e: SerializationException) {
                handleSerializationException(httpResponse, e)
            }
        } ?: handleEmptyBody(httpResponse)

    private fun processHttpBadRequestResponse(httpResponse: Response): ResponseWrapper =
        httpResponse.body?.let { responseBody ->
            try {
                val paymentError =
                    json.parse(PaymentError.serializer(), responseBody.string())

                ErrorResponseWrapper(
                    BadRequestException(paymentError.message, paymentError)
                )
            } catch (e: SerializationException) {
                handleSerializationException(httpResponse, e)
            }
        } ?: handleEmptyBody(httpResponse)

    private fun processHttpUnauthorizedResponse(httpResponse: Response): ResponseWrapper =
        httpResponse.body?.let { responseBody ->
            try {
                val genericError =
                    json.parse(GenericError.serializer(), responseBody.string())

                ErrorResponseWrapper(
                    BadRequestException(genericError.error, genericError)
                )
            } catch (e: SerializationException) {
                handleSerializationException(httpResponse, e)
            }
        } ?: handleEmptyBody(httpResponse)

    private fun handleEmptyBody(httpResponse: Response): ErrorResponseWrapper {
        val message = "Backend response with empty body. HTTP response: " +
                "${httpResponse.code}, $httpResponse.message"
        logger.e(TAG, message)
        return ErrorResponseWrapper(InternalException(message))
    }

    private fun processOtherHttpErrorResponse(httpResponse: Response): ResponseWrapper {
        logger.e(TAG, "HTTP response: ${httpResponse.code}, ${httpResponse.message}")
        return ErrorResponseWrapper(
            HttpException(
                httpResponse.code,
                httpResponse.message
            )
        )
    }

    private fun handleSerializationException(httpResponse: Response, e: SerializationException): ErrorResponseWrapper {
        val message =
            "Backend response deserialization problem. Backend response: " +
                    "${httpResponse.code}, ${httpResponse.message}"
        logger.e(TAG, message, e)
        return ErrorResponseWrapper(InternalException(message, e))
    }
}
