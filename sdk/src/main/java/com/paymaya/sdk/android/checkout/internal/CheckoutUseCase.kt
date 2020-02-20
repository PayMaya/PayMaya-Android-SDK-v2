package com.paymaya.sdk.android.checkout.internal

import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.common.internal.RedirectSuccessResponseWrapper
import com.paymaya.sdk.android.common.internal.SendRequestBaseUseCase
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody

internal class CheckoutUseCase(
    json: Json,
    private val repository: CheckoutRepository
) : SendRequestBaseUseCase<CheckoutRequest>(json) {

    override suspend fun sendRequest(request: CheckoutRequest): Response =
        repository.checkout(request)

    override fun prepareSuccessResponse(responseBody: ResponseBody): RedirectSuccessResponseWrapper {
        val checkoutResponse = json.parse(CheckoutResponse.serializer(), responseBody.string())

        // TODO check if really needed
        // if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
        //   redirectUrl += "&cssfix=true"
        // }

        return RedirectSuccessResponseWrapper(
            checkoutResponse.checkoutId,
            checkoutResponse.redirectUrl
        )
    }
}
