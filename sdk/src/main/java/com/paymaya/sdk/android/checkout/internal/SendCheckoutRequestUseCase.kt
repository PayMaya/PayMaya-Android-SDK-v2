package com.paymaya.sdk.android.checkout.internal

import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.checkout.models.CheckoutResponse
import com.paymaya.sdk.android.common.internal.SendRequestBaseUseCase
import com.paymaya.sdk.android.common.internal.SuccessResponse
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody

internal class SendCheckoutRequestUseCase(
    json: Json,
    private val repository: CheckoutRepository
) : SendRequestBaseUseCase<CheckoutRequest>(json) {

    override suspend fun sendRequest(request: CheckoutRequest): Response =
        repository.checkout(request)

    override fun prepareSuccessResponse(responseBody: ResponseBody): SuccessResponse {
        val checkoutResponse = json.parse(CheckoutResponse.serializer(), responseBody.string())

        // TODO check if really needed
        // if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
        //   redirectUrl += "&cssfix=true"
        // }

        return SuccessResponse(
            checkoutResponse.checkoutId,
            checkoutResponse.redirectUrl
        )
    }
}
