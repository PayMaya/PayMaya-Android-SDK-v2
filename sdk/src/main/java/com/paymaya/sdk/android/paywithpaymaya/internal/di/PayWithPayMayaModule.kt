package com.paymaya.sdk.android.paywithpaymaya.internal.di

import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentPresenter
import com.paymaya.sdk.android.paywithpaymaya.internal.CreateWalletLinkUseCase
import com.paymaya.sdk.android.paywithpaymaya.internal.PayWithPayMayaRepository
import com.paymaya.sdk.android.paywithpaymaya.internal.SinglePaymentUseCase

internal object PayWithPayMayaModule {

    fun getPayWithPayMayaRepository(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ) =
        PayWithPayMayaRepository(
            environment,
            clientKey,
            CommonModule.getJson(),
            CommonModule.getHttpClient(logLevel)
        )

    fun getCreateWalletLinkUseCase(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ) =
        CreateWalletLinkUseCase(
            CommonModule.getJson(),
            getPayWithPayMayaRepository(environment, clientKey, logLevel),
            CommonModule.getLogger(logLevel)
        )

    fun getCreateWalletLinkPresenter(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ) =
        PayMayaPaymentPresenter(getCreateWalletLinkUseCase(environment, clientKey, logLevel))

    fun getSinglePaymentUseCase(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ) =
        SinglePaymentUseCase(
            CommonModule.getJson(),
            getPayWithPayMayaRepository(environment, clientKey, logLevel),
            CommonModule.getLogger(logLevel)
        )

    fun getSinglePaymentPresenter(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ) =
        PayMayaPaymentPresenter(getSinglePaymentUseCase(environment, clientKey, logLevel))
}
