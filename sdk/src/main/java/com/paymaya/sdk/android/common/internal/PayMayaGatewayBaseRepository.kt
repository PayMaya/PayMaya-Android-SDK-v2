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

package com.paymaya.sdk.android.common.internal

import android.util.Base64
import com.paymaya.sdk.android.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

internal abstract class PayMayaGatewayBaseRepository(
    protected val json: Json,
    protected val httpClient: OkHttpClient
) {

    protected fun getBaseRequestBuilder(clientPublicKey: String, length: Int) =
        Request.Builder()
            .header(HEADER_AUTHORIZATION, prepareAuthorizationValue(clientPublicKey))
            .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON)
            .header(HEADER_CONTENT_LENGTH, length.toString())
            .header(HEADER_X_PAYMAYA_SDK, "$SDK_VERSION_PREFIX${BuildConfig.VERSION_NAME}")

    private fun prepareAuthorizationValue(clientPublicKey: String): String {
        val authorization = "$clientPublicKey:" // Note: password is empty
        val authorizationEncoded = String(
            Base64.encode(authorization.toByteArray(Charsets.UTF_8), Base64.NO_WRAP),
            Charsets.UTF_8
        )
        return "$AUTH_BASIC_PREFIX $authorizationEncoded"
    }

    companion object {
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_CONTENT_LENGTH = "Content-Length"
        private const val HEADER_X_PAYMAYA_SDK = "x-paymaya-sdk"
        private const val MIME_APPLICATION_JSON = "application/json"
        private const val SDK_VERSION_PREFIX = "android-v"
        private const val AUTH_BASIC_PREFIX = "Basic"
    }
}