package com.paymaya.sdk.android.common.internal

import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.exceptions.HttpException
import com.paymaya.sdk.android.common.exceptions.InternalException
import com.paymaya.sdk.android.common.models.GenericError
import com.paymaya.sdk.android.common.models.PaymentError
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody
import java.net.HttpURLConnection

internal abstract class SendRequestBaseUseCase<T>(
    protected val json: Json
) {

    suspend fun run(request: T): ResponseWrapper =
        try {
            processResponse(
                sendRequest(request)
            )
        } catch (e: Exception) {
            ErrorResponse(e)
        }

    abstract suspend fun sendRequest(request: T): Response

    abstract fun prepareSuccessResponse(responseBody: ResponseBody): SuccessResponse

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

                ErrorResponse(
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

                ErrorResponse(
                    BadRequestException(genericError.error, genericError)
                )
            } catch (e: SerializationException) {
                handleSerializationException(httpResponse, e)
            }
        } ?: handleEmptyBody(httpResponse)

    private fun handleEmptyBody(httpResponse: Response): ErrorResponse {
        val message = "Backend response with empty body. HTTP response: " +
                "${httpResponse.code}, $httpResponse.message"
        Logger.e(TAG, message)
        return ErrorResponse(InternalException(message))
    }

    private fun processOtherHttpErrorResponse(httpResponse: Response): ResponseWrapper {
        Logger.e(TAG, "HTTP response: ${httpResponse.code}, ${httpResponse.message}")
        return ErrorResponse(
            HttpException(
                httpResponse.code,
                httpResponse.message
            )
        )
    }

    private fun handleSerializationException(httpResponse: Response, e: SerializationException): ResponseWrapper {
        val message =
            "Backend response deserialization problem. Backend response: " +
                    "${httpResponse.code}, ${httpResponse.message}"
        Logger.e(TAG, message, e)
        return ErrorResponse(InternalException(message, e))
    }

    companion object {
        private const val TAG = "SendRequestBaseUseCase"
    }
}
