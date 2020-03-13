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

package com.paymaya.sdk.android.checkout.internal.di

import com.paymaya.sdk.android.checkout.internal.CheckoutRepository
import com.paymaya.sdk.android.checkout.internal.CheckoutUseCase
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentPresenter

@Suppress("MemberVisibilityCanBePrivate")
internal object CheckoutModule {

    fun getCheckoutRepository(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        CheckoutRepository(
            environment,
            clientPublicKey,
            CommonModule.getJson(),
            CommonModule.getHttpClient(logLevel)
        )

    fun getCheckoutUseCase(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        CheckoutUseCase(
            CommonModule.getJson(),
            getCheckoutRepository(environment, clientPublicKey, logLevel),
            CommonModule.getLogger(logLevel)
        )

    fun getCheckoutPresenter(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        PayMayaPaymentPresenter(
            getCheckoutUseCase(environment, clientPublicKey, logLevel),
            CommonModule.getCheckStatusUseCase(environment, clientPublicKey, logLevel),
            CommonModule.getLogger(logLevel)
        )
}
