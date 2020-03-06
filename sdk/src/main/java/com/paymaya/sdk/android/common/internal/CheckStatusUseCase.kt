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
        val response = json.parse(StatusResponse.serializer(), responseBody.string())

        // TODO check if really needed
        // if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
        //   redirectUrl += "&cssfix=true"
        // }

        return StatusSuccessResponseWrapper(
            response.id,
            response.status
        )
    }
}
