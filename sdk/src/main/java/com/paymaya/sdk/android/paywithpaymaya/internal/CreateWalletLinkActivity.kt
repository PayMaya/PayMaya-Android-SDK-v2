package com.paymaya.sdk.android.paywithpaymaya.internal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentContract
import com.paymaya.sdk.android.paywithpaymaya.internal.di.PayWithPayMayaModule
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest

internal class CreateWalletLinkActivity : PayMayaPaymentActivity<CreateWalletLinkRequest>() {

    override fun buildPresenter(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ): PayMayaPaymentContract.Presenter<CreateWalletLinkRequest> =
        PayWithPayMayaModule.getCreateWalletLinkPresenter(environment, clientPublicKey, logLevel)

    companion object {
        fun newIntent(
            activity: Activity,
            request: CreateWalletLinkRequest,
            clientPublicKey: String,
            environment: PayMayaEnvironment,
            logLevel: LogLevel
        ): Intent {
            val bundle = Bundle()
            bundle.putParcelable(EXTRAS_REQUEST, request)
            val intent = Intent(activity, CreateWalletLinkActivity::class.java)
            intent.putExtra(EXTRAS_BUNDLE, bundle)
            intent.putExtra(EXTRAS_CLIENT_PUBLIC_KEY, clientPublicKey)
            intent.putExtra(EXTRAS_ENVIRONMENT, environment)
            intent.putExtra(EXTRAS_LOG_LEVEL, logLevel)
            return intent
        }
    }
}
