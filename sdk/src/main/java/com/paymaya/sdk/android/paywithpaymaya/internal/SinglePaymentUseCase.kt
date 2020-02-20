package com.paymaya.sdk.android.paywithpaymaya.internal

import com.paymaya.sdk.android.common.internal.RedirectSuccessResponseWrapper
import com.paymaya.sdk.android.common.internal.SendRequestBaseUseCase
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import com.paymaya.sdk.android.paywithpaymaya.internal.models.SinglePaymentResponse
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody

internal class SinglePaymentUseCase(
    json: Json,
    private val repository: PayWithPayMayaRepository
) : SendRequestBaseUseCase<SinglePaymentRequest>(json) {

    override suspend fun sendRequest(request: SinglePaymentRequest): Response =
        repository.singlePayment(request)

    override fun prepareSuccessResponse(responseBody: ResponseBody): RedirectSuccessResponseWrapper {
        val singlePaymentResponse = json.parse(SinglePaymentResponse.serializer(), responseBody.string())

        // TODO check if really needed
        // if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
        //   redirectUrl += "&cssfix=true"
        // }

        return RedirectSuccessResponseWrapper(
            singlePaymentResponse.paymentId,
            singlePaymentResponse.redirectUrl
        )
    }
}
