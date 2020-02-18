package com.paymaya.sdk.android.vault.internal

import com.paymaya.sdk.android.common.internal.SuccessResponseWrapper
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse

internal class TokenizeCardSuccessResponseWrapper(
    val response: TokenizeCardResponse
) : SuccessResponseWrapper()