package com.paymaya.sdk.android.checkout.internal

import com.paymaya.sdk.android.BuildConfig
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.PayMayaGatewayBaseRepository
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await

internal class CheckoutRepository(
    environment: PayMayaEnvironment,
    private val clientPublicKey: String,
    json: Json,
    httpClient: OkHttpClient
) : PayMayaGatewayBaseRepository(json, httpClient) {

    val baseUrl = when (environment) {
        PayMayaEnvironment.PRODUCTION -> BuildConfig.API_CHECKOUT_BASE_URL_PRODUCTION
        PayMayaEnvironment.SANDBOX -> BuildConfig.API_CHECKOUT_BASE_URL_SANDBOX
    }

    suspend fun checkout(requestModel: CheckoutRequest): Response {
        val authorizationValue = prepareAuthorizationValue(clientPublicKey)
        val bodyString = json.stringify(CheckoutRequest.serializer(), requestModel)
        val requestBody = bodyString.toRequestBody()

        val request = Request.Builder()
            .url(baseUrl + CHECKOUT_ENDPOINT)
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

    companion object {
        private const val CHECKOUT_ENDPOINT = "checkouts"
    }
}