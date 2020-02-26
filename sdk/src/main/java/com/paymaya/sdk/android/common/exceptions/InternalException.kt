package com.paymaya.sdk.android.common.exceptions

/**
 * InternalException class defines internal exceptions.
 *
 * @property message The detail message of exception.
 * @property cause The exceptions cause.
 */
class InternalException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
