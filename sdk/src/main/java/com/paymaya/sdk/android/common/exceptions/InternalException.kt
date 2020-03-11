package com.paymaya.sdk.android.common.exceptions

/**
 * Unexpected response from the backed was retrieved.
 *
 * @property message Detail message.
 * @property cause Cause (optional).
 */
class InternalException internal constructor(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
