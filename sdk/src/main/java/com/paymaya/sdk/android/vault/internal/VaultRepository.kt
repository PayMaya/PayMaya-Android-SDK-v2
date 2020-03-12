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

package com.paymaya.sdk.android.vault.internal

import com.paymaya.sdk.android.BuildConfig
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.PayMayaGatewayBaseRepository
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardRequest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await

internal class VaultRepository(
    environment: PayMayaEnvironment,
    private val clientPublicKey: String,
    json: Json,
    httpClient: OkHttpClient
) : PayMayaGatewayBaseRepository(json, httpClient) {

    val baseUrl = when (environment) {
        PayMayaEnvironment.PRODUCTION -> "$BASE_URL_PRODUCTION$BASE_URL_SUFFIX"
        PayMayaEnvironment.SANDBOX -> "$BASE_URL_SANDBOX$BASE_URL_SUFFIX"
    }

    suspend fun tokenizeCard(requestModel: TokenizeCardRequest): Response {
        val authorizationValue = prepareAuthorizationValue(clientPublicKey)
        val bodyString = json.stringify(TokenizeCardRequest.serializer(), requestModel)
        val requestBody = bodyString.toRequestBody()

        val request = Request.Builder()
            .url(baseUrl + VAULT_CREATE_TOKEN)
            .header(
                HEADER_CONTENT_TYPE,
                MIME_APPLICATION_JSON
            )
            .header(HEADER_AUTHORIZATION, authorizationValue)
            .header(HEADER_CONTENT_LENGTH, bodyString.length.toString())
            .header(HEADER_X_PAYMAYA_SDK, "$SDK_VERSION_PREFIX${BuildConfig.VERSION_NAME}")
            .post(requestBody)
            .build()

        return httpClient.newCall(request).await()
    }

    suspend fun status(id: String): Response {
        val authorizationValue = prepareAuthorizationValue(clientPublicKey)

        val request = Request.Builder()
            .url("$baseUrl$PAYMENTS_ENDPOINT/$id/$STATUS_ENDPOINT")
            .header(
                HEADER_CONTENT_TYPE,
                MIME_APPLICATION_JSON
            )
            .header(HEADER_AUTHORIZATION, authorizationValue)
            .header(HEADER_CONTENT_LENGTH, "0")
            .header(HEADER_X_PAYMAYA_SDK, "$SDK_VERSION_PREFIX${BuildConfig.VERSION_NAME}")
            .get()
            .build()

        return httpClient.newCall(request).await()
    }

    companion object {
        private const val PAYMENTS_ENDPOINT = "payments"
        private const val STATUS_ENDPOINT = "status"
        private const val VAULT_CREATE_TOKEN = "payment-tokens"
        private const val BASE_URL_SUFFIX = "/payments/v1/"

        const val BASE_URL_PRODUCTION = "https://pg.paymaya.com"
        const val BASE_URL_SANDBOX = "https://pg-sandbox.paymaya.com"
    }
}