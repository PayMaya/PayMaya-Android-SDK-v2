package com.paymaya.sdk.android.vault.internal

import com.paymaya.sdk.android.common.internal.Logger
import com.paymaya.sdk.android.common.internal.SendRequestBaseUseCase
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardRequest
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.ResponseBody

internal class TokenizeCardUseCase(
    json: Json,
    private val repository: VaultRepository,
    logger: Logger
) : SendRequestBaseUseCase<TokenizeCardRequest>(json, logger) {

    override suspend fun sendRequest(request: TokenizeCardRequest): Response =
        repository.tokenizeCard(request)

    override fun prepareSuccessResponse(responseBody: ResponseBody): TokenizeCardSuccessResponseWrapper {
        val response = json.parse(TokenizeCardResponse.serializer(), responseBody.string())

        return TokenizeCardSuccessResponseWrapper(response)
    }
}
