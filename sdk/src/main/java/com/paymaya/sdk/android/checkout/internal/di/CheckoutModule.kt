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
        clientKey: String,
        logLevel: LogLevel
    ) =
        CheckoutRepository(
            environment,
            clientKey,
            CommonModule.getJson(),
            CommonModule.getHttpClient(logLevel)
        )

    fun getCheckoutUseCase(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ) =
        CheckoutUseCase(
            CommonModule.getJson(),
            getCheckoutRepository(environment, clientKey, logLevel),
            CommonModule.getLogger(logLevel)
        )

    fun getCheckoutPresenter(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ) =
        PayMayaPaymentPresenter(getCheckoutUseCase(environment, clientKey, logLevel))
}
