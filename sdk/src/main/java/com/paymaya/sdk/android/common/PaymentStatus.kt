/*
 * Copyright (c) 2020  PayMaya Philippines, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.paymaya.sdk.android.common

/**
 * Payment status retrieved when checking status of a payment.
 */
enum class PaymentStatus {

    /**
     * Token is pending
     */
    PENDING_TOKEN,

    /**
     * Initial payment status of the checkout transaction
     */
    PENDING_PAYMENT,

    /**
     * When payment transaction is not executed and expiration time has been reached.
     */
    PAYMENT_EXPIRED,

    /**
     * When payment transaction is waiting for authentication.
     */
    FOR_AUTHENTICATION,

    /**
     * When payment transaction is currently authenticating.
     */
    AUTHENTICATING,

    /**
     * When authentication has been successfully executed.
     * Authentication may be in the form of 3DS authentication for card transactions.
     */
    AUTH_SUCCESS,

    /**
     * When authentication of payment has failed.
     */
    AUTH_FAILED,

    /**
     * When payment is processing.
     */
    PAYMENT_PROCESSING,

    /**
     * When payment is successfully processed.
     */
    PAYMENT_SUCCESS,

    /**
     * When payment is not successfully processed.
     */
    PAYMENT_FAILED,

    /**
     * When a successfully processed payment has been reversed (usually before a settlement cut-off for card-based payments).
     */
    VOIDED,

    /**
     * When a successfully processed payment has been fully or partially reversed (usually after a settlement cut-off for card-based payments).
     */
    REFUNDED
}
