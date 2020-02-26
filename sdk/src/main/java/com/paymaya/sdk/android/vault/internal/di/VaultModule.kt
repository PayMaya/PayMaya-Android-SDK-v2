package com.paymaya.sdk.android.vault.internal.di

import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.vault.internal.CardInfoValidator
import com.paymaya.sdk.android.vault.internal.TokenizeCardUseCase
import com.paymaya.sdk.android.vault.internal.VaultRepository
import com.paymaya.sdk.android.vault.internal.screen.TokenizeCardPresenter
import java.util.*

internal object VaultModule {

    fun getVaultRepository(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ) =
        VaultRepository(
            environment,
            clientKey,
            CommonModule.getJson(),
            CommonModule.getHttpClient(logLevel)
        )

    fun getTokenizeCardUseCase(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ) =
        TokenizeCardUseCase(
            CommonModule.getJson(),
            getVaultRepository(environment, clientKey, logLevel),
            CommonModule.getLogger(logLevel)
        )

    fun getCardInfoValidator(date: Calendar) =
        CardInfoValidator(date)

    fun getTokenizeCardPresenter(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel,
        date: Calendar
    ) =
        TokenizeCardPresenter(
            getTokenizeCardUseCase(environment, clientKey, logLevel),
            getCardInfoValidator(date),
            CommonModule.getLogger(logLevel)
        )
}
