package com.paymaya.sdk.android.common.internal

import android.util.Base64
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

internal abstract class PayMayaGatewayBaseRepository(
    protected val json: Json,
    protected val httpClient: OkHttpClient
) {

    protected fun prepareAuthorizationValue(clientKey: String): String {
        val authorization = "$clientKey:" // Note: password is empty
        val authorizationEncoded = String(
            Base64.encode(authorization.toByteArray(Charsets.UTF_8), Base64.NO_WRAP),
            Charsets.UTF_8
        )
        return "$AUTH_BASIC_PREFIX $authorizationEncoded"
    }

    companion object {
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_CONTENT_LENGTH = "Content-Length"
        const val MIME_APPLICATION_JSON = "application/json"
        private const val AUTH_BASIC_PREFIX = "Basic"
    }
}