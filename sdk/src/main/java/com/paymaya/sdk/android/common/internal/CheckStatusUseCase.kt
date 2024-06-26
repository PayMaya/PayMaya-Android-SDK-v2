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

import com.paymaya.sdk.android.common.internal.models.StatusResponse
import com.paymaya.sdk.android.vault.internal.VaultRepository
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody

internal class CheckStatusUseCase(
    json: Json,
    private val repository: VaultRepository,
    logger: Logger
) : SendRequestBaseUseCase<String>(json, logger) {

    override suspend fun sendRequest(request: String): Response =
        repository.status(request)

    override fun prepareSuccessResponse(responseBody: ResponseBody): StatusSuccessResponseWrapper {
        val response = json.decodeFromString(StatusResponse.serializer(), responseBody.string())

        return StatusSuccessResponseWrapper(
            response.id,
            response.status
        )
    }
}
