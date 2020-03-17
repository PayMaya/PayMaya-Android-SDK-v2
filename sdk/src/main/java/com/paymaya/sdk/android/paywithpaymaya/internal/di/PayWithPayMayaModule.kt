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

package com.paymaya.sdk.android.paywithpaymaya.internal.di

import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentPresenter
import com.paymaya.sdk.android.paywithpaymaya.internal.CreateWalletLinkUseCase
import com.paymaya.sdk.android.paywithpaymaya.internal.PayWithPayMayaRepository
import com.paymaya.sdk.android.paywithpaymaya.internal.SinglePaymentUseCase

@Suppress("MemberVisibilityCanBePrivate")
internal object PayWithPayMayaModule {

    fun getPayWithPayMayaRepository(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        PayWithPayMayaRepository(
            environment,
            clientPublicKey,
            CommonModule.getJson(),
            CommonModule.getHttpClient(logLevel)
        )

    fun getCreateWalletLinkUseCase(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        CreateWalletLinkUseCase(
            CommonModule.getJson(),
            getPayWithPayMayaRepository(environment, clientPublicKey, logLevel),
            CommonModule.getLogger(logLevel)
        )

    fun getCreateWalletLinkPresenter(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        PayMayaPaymentPresenter(
            getCreateWalletLinkUseCase(environment, clientPublicKey, logLevel),
            CommonModule.getCheckStatusUseCase(environment, clientPublicKey, logLevel),
            CommonModule.getLogger(logLevel),
            paymentStatusAutoCheck = false
        )

    fun getSinglePaymentUseCase(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        SinglePaymentUseCase(
            CommonModule.getJson(),
            getPayWithPayMayaRepository(environment, clientPublicKey, logLevel),
            CommonModule.getLogger(logLevel)
        )

    fun getSinglePaymentPresenter(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        PayMayaPaymentPresenter(
            getSinglePaymentUseCase(environment, clientPublicKey, logLevel),
            CommonModule.getCheckStatusUseCase(environment, clientPublicKey, logLevel),
            CommonModule.getLogger(logLevel)
        )
}
