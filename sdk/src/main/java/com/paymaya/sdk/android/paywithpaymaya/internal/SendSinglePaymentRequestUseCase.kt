package com.paymaya.sdk.android.paywithpaymaya.internal

import com.paymaya.sdk.android.common.internal.SendRequestBaseUseCase
import com.paymaya.sdk.android.common.internal.SuccessResponse
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentResponse
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody

internal class SendSinglePaymentRequestUseCase(
    json: Json,
    private val repository: PayWithPayMayaRepository
) : SendRequestBaseUseCase<SinglePaymentRequest>(json) {

    override suspend fun sendRequest(request: SinglePaymentRequest): Response =
        repository.singlePayment(request)

    override fun prepareSuccessResponse(responseBody: ResponseBody): SuccessResponse {
        val payWithPayMayaResponse = json.parse(SinglePaymentResponse.serializer(), responseBody.string())

        // TODO check if really needed
        // if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
        //   redirectUrl += "&cssfix=true"
        // }

        return SuccessResponse(
            payWithPayMayaResponse.paymentId,
            payWithPayMayaResponse.redirectUrl
        )
    }
}
