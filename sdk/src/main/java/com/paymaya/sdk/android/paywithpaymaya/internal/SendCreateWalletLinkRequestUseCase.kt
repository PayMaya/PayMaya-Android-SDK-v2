package com.paymaya.sdk.android.paywithpaymaya.internal

import com.paymaya.sdk.android.common.internal.SendRequestBaseUseCase
import com.paymaya.sdk.android.common.internal.SuccessResponse
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkResponse
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody

internal class SendCreateWalletLinkRequestUseCase(
    json: Json,
    private val repository: PayWithPayMayaRepository
) : SendRequestBaseUseCase<CreateWalletLinkRequest>(json) {

    override suspend fun sendRequest(request: CreateWalletLinkRequest): Response =
        repository.createWalletLink(request)

    override fun prepareSuccessResponse(responseBody: ResponseBody): SuccessResponse {
        val payWithPayMayaResponse = json.parse(CreateWalletLinkResponse.serializer(), responseBody.string())

        // TODO check if really needed
        // if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
        //   redirectUrl += "&cssfix=true"
        // }

        return SuccessResponse(
            payWithPayMayaResponse.linkId,
            payWithPayMayaResponse.redirectUrl
        )
    }
}
