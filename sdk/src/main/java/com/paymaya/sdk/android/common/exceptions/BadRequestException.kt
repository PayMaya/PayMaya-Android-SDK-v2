package com.paymaya.sdk.android.common.exceptions

import com.paymaya.sdk.android.common.models.BaseError

class BadRequestException(
    message: String,
    val error: BaseError
) : Exception(message)
