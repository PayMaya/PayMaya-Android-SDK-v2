package com.paymaya.sdk.android.common.exceptions

/**
 * Unknown Http exception occurred.
 *
 * @property code Http response code.
 * @property message Http response message.
 */
class HttpException internal constructor(
    val code: Int,
    message: String
) : Exception(message)
