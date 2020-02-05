package com.paymaya.sdk.android.internal

import android.util.Base64
import com.paymaya.sdk.android.BuildConfig
import com.paymaya.sdk.android.PayMayaEnvironment
import com.paymaya.sdk.android.checkout.models.Checkout
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await

internal class PayMayaGatewayRepository(
    environment: PayMayaEnvironment,
    private val clientKey: String,
    private val json: Json,
    private val httpClient: OkHttpClient
) {

    val checkoutBaseUrl = when (environment) {
        PayMayaEnvironment.PRODUCTION -> BuildConfig.API_CHECKOUT_BASE_URL_PRODUCTION
        PayMayaEnvironment.SANDBOX -> BuildConfig.API_CHECKOUT_BASE_URL_SANDBOX
    }

    suspend fun checkout(checkoutModel: Checkout): Response {
        val authorization = "$clientKey:"
        val authorizationEncoded = String(
            Base64.encode(authorization.toByteArray(Charsets.UTF_8), Base64.NO_WRAP),
            Charsets.UTF_8
        )
        val bodyString = json.stringify(Checkout.serializer(), checkoutModel)
        val requestBody = bodyString.toRequestBody()

        val request = Request.Builder()
            .url(checkoutBaseUrl + CHECKOUT_ENDPOINT)
            .header(
                HEADER_CONTENT_TYPE,
                MIME_APPLICATION_JSON
            )
            .header(
                HEADER_AUTHORIZATION,
                "$AUTH_BASIC_PREFIX $authorizationEncoded"
            )
            .header(HEADER_CONTENT_LENGTH, bodyString.length.toString())
            .post(requestBody)
            .build()

        return httpClient.newCall(request).await()
    }

    companion object {
        private const val CHECKOUT_ENDPOINT = "checkouts"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_CONTENT_LENGTH = "Content-Length"
        private const val MIME_APPLICATION_JSON = "application/json"
        private const val AUTH_BASIC_PREFIX = "Basic"
    }
}