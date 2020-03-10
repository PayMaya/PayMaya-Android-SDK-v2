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
        PayMayaEnvironment.PRODUCTION -> BuildConfig.API_VAULT_BASE_URL_PRODUCTION
        PayMayaEnvironment.SANDBOX -> BuildConfig.API_VAULT_BASE_URL_SANDBOX
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
    }
}