package com.paymaya.sdk.android.checkout.internal

import com.paymaya.sdk.android.checkout.exceptions.BadRequestException
import com.paymaya.sdk.android.checkout.exceptions.HttpException
import com.paymaya.sdk.android.checkout.exceptions.InternalException
import com.paymaya.sdk.android.checkout.models.Checkout
import com.paymaya.sdk.android.checkout.models.CheckoutError
import com.paymaya.sdk.android.checkout.models.CheckoutResponse
import com.paymaya.sdk.android.checkout.models.GenericError
import com.paymaya.sdk.android.internal.Logger
import com.paymaya.sdk.android.internal.PayMayaGatewayRepository
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Response
import java.net.HttpURLConnection

internal class SendCheckoutRequestUseCase(
    private val json: Json,
    private val payMayaGatewayRepository: PayMayaGatewayRepository
) {

    suspend fun run(checkoutModel: Checkout): ResponseWrapper =
        try {
            processResponse(payMayaGatewayRepository.checkout(checkoutModel))
        } catch (e: Exception) {
            ErrorResponse(e)
        }

    private fun processResponse(response: Response): ResponseWrapper =
        when (response.code) {
            HttpURLConnection.HTTP_OK -> processHttpOkResponse(response)
            HttpURLConnection.HTTP_BAD_REQUEST -> processHttpBadRequestResponse(response)
            HttpURLConnection.HTTP_UNAUTHORIZED -> processHttpUnauthorizedResponse(response)
            else -> processOtherHttpErrorResponse(response)
        }

    private fun processHttpOkResponse(response: Response): ResponseWrapper =
        response.body?.let { responseBody ->
            try {
                val checkoutResponse = json.parse(CheckoutResponse.serializer(), responseBody.string())

                // TODO check if really needed
                // if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
                //   redirectUrl += "&cssfix=true"
                // }

                SuccessResponse(checkoutResponse.redirectUrl, checkoutResponse.checkoutId)
            } catch (e: SerializationException) {
                handleSerializationException(response, e)
            }
        } ?: handleEmptyBody(response)

    private fun processHttpBadRequestResponse(response: Response): ResponseWrapper =
        response.body?.let { responseBody ->
            try {
                val checkoutError =
                    json.parse(CheckoutError.serializer(), responseBody.string())

                ErrorResponse(BadRequestException(checkoutError.message, checkoutError))
            } catch (e: SerializationException) {
                handleSerializationException(response, e)
            }
        } ?: handleEmptyBody(response)

    private fun processHttpUnauthorizedResponse(response: Response): ResponseWrapper =
        response.body?.let { responseBody ->
            try {
                val genericError =
                    json.parse(GenericError.serializer(), responseBody.string())

                ErrorResponse(BadRequestException(genericError.error, genericError))
            } catch (e: SerializationException) {
                handleSerializationException(response, e)
            }
        } ?: handleEmptyBody(response)

    private fun handleEmptyBody(response: Response): ErrorResponse {
        val message = "Backend response with empty body. HTTP response: " +
                "${response.code}, $response.message"
        Logger.e(TAG, message)
        return ErrorResponse(InternalException(message))
    }

    private fun processOtherHttpErrorResponse(response: Response): ResponseWrapper {
        Logger.e(TAG, "HTTP response: ${response.code}, ${response.message}")
        return ErrorResponse(HttpException(response.code, response.message))
    }

    private fun handleSerializationException(response: Response, e: SerializationException): ResponseWrapper {
        val message =
            "Backend response deserialization problem. Backend response: " +
                    "${response.code}, ${response.message}"
        Logger.e(TAG, message, e)
        return ErrorResponse(InternalException(message, e))
    }

    companion object {
        private const val TAG = "SendCheckoutRequestUseCase"
    }
}
