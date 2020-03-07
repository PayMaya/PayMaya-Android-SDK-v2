package com.paymaya.sdk.android.common.exceptions

import com.paymaya.sdk.android.common.models.BaseError

/**
 * Server returned 400 (Bad request) or 401 (Unauthorized) code.
 *
 * @property message Detail message of exception.
 * @property error Detailed error.
 */
class BadRequestException internal constructor(
    message: String,
    val error: BaseError
) : Exception(message)
