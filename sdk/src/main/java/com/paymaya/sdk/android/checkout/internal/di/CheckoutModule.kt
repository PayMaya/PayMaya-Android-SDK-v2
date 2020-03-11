package com.paymaya.sdk.android.checkout.internal.di

import com.paymaya.sdk.android.checkout.internal.CheckoutRepository
import com.paymaya.sdk.android.checkout.internal.CheckoutUseCase
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentPresenter

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
