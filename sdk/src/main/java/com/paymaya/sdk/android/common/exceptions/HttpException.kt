package com.paymaya.sdk.android.common.exceptions

/**
 * HttpException class describes an exception that occurred during the processing of HTTP requests.
 *
 * @property code The event code that are associated with the HTTP exception.
 * @property message The detail message of exception.
 * @property cause The exceptions cause.
 */
class HttpException(
    val code: Int,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
