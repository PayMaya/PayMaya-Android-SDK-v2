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
