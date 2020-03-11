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

import com.paymaya.sdk.android.common.internal.Logger
import com.paymaya.sdk.android.common.internal.RedirectSuccessResponseWrapper
import com.paymaya.sdk.android.common.internal.SendRequestBaseUseCase
import com.paymaya.sdk.android.paywithpaymaya.internal.models.CreateWalletLinkResponse
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody

internal class CreateWalletLinkUseCase(
    json: Json,
    private val repository: PayWithPayMayaRepository,
    logger: Logger
) : SendRequestBaseUseCase<CreateWalletLinkRequest>(json, logger) {

    override suspend fun sendRequest(request: CreateWalletLinkRequest): Response =
        repository.createWalletLink(request)

    override fun prepareSuccessResponse(responseBody: ResponseBody): RedirectSuccessResponseWrapper {
        val createWalletLinkResponse = json.parse(CreateWalletLinkResponse.serializer(), responseBody.string())

        return RedirectSuccessResponseWrapper(
            createWalletLinkResponse.linkId,
            createWalletLinkResponse.redirectUrl
        )
    }
}
