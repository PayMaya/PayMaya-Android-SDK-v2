package com.paymaya.sdk.android.common.exceptions

import com.paymaya.sdk.android.common.models.BaseError

/**
 * BadRequestException class describes an exception that occurred during app call unexpected request.
 *
 * @property message The detail message of exception.
 * @property error Model of base error which grouping related exceptions classes.
 */
class BadRequestException(
    message: String,
    val error: BaseError
) : Exception(message)
