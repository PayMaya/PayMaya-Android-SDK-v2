package com.paymaya.sdk.android.vault.internal.di

import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.vault.internal.helpers.CardTypeDetector
import com.paymaya.sdk.android.vault.internal.TokenizeCardUseCase
import com.paymaya.sdk.android.vault.internal.VaultRepository
import com.paymaya.sdk.android.vault.internal.helpers.CardInfoValidator
import com.paymaya.sdk.android.vault.internal.screen.TokenizeCardPresenter
import java.util.*

internal object VaultModule {

    fun getVaultRepository(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        VaultRepository(
            environment,
            clientPublicKey,
            CommonModule.getJson(),
            CommonModule.getHttpClient(logLevel)
        )

    fun getTokenizeCardUseCase(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ) =
        TokenizeCardUseCase(
            CommonModule.getJson(),
            getVaultRepository(environment, clientPublicKey, logLevel),
            CommonModule.getLogger(logLevel)
        )

    fun getCardInfoValidator(date: Calendar) =
        CardInfoValidator(date)

    fun getCardTypeDetector() =
        CardTypeDetector()

    fun getTokenizeCardPresenter(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel,
        date: Calendar
    ) =
        TokenizeCardPresenter(
            getTokenizeCardUseCase(environment, clientPublicKey, logLevel),
            getCardInfoValidator(date),
            getCardTypeDetector(),
            CommonModule.getLogger(logLevel)
        )
}
