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

package com.paymaya.sdk.android.paywithpaymaya.internal

import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.PayMayaGatewayBaseRepository
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await

internal class PayWithPayMayaRepository(
    environment: PayMayaEnvironment,
    private val clientPublicKey: String,
    json: Json,
    httpClient: OkHttpClient
) : PayMayaGatewayBaseRepository(json, httpClient) {

    val baseUrl = when (environment) {
        PayMayaEnvironment.PRODUCTION -> "$BASE_URL_PRODUCTION$BASE_URL_SUFFIX"
        PayMayaEnvironment.SANDBOX -> "$BASE_URL_SANDBOX$BASE_URL_SUFFIX"
    }

    suspend fun singlePayment(requestModel: SinglePaymentRequest): Response {
        val bodyString = json.stringify(SinglePaymentRequest.serializer(), requestModel)

        val request = getBaseRequestBuilder(clientPublicKey, bodyString.length)
            .url(baseUrl + SINGLE_PAYMENT_ENDPOINT)
            .post(bodyString.toRequestBody())
            .build()

        return httpClient.newCall(request).await()
    }

    suspend fun createWalletLink(requestModel: CreateWalletLinkRequest): Response {
        val bodyString = json.stringify(CreateWalletLinkRequest.serializer(), requestModel)

        val request = getBaseRequestBuilder(clientPublicKey, bodyString.length)
            .url(baseUrl + CREATE_WALLET_LINK_ENDPOINT)
            .post(bodyString.toRequestBody())
            .build()

        return httpClient.newCall(request).await()
    }

    companion object {
        const val BASE_URL_PRODUCTION = "https://pg.paymaya.com"
        const val BASE_URL_SANDBOX = "https://pg-sandbox.paymaya.com"

        private const val BASE_URL_SUFFIX = "/payby/v2/paymaya/"

        private const val SINGLE_PAYMENT_ENDPOINT = "payments"
        private const val CREATE_WALLET_LINK_ENDPOINT = "link"
    }
}