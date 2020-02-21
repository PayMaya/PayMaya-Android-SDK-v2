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
        clientKey: String,
        logLevel: LogLevel
    ): PayMayaPaymentContract.Presenter<CreateWalletLinkRequest> =
        PayWithPayMayaModule.getCreateWalletLinkPresenter(environment, clientKey, logLevel)

    companion object {
        fun newIntent(
            activity: Activity,
            requestData: CreateWalletLinkRequest,
            clientKey: String,
            environment: PayMayaEnvironment
        ): Intent {
            val bundle = Bundle()
            bundle.putParcelable(EXTRAS_REQUEST_DATA, requestData)
            val intent = Intent(activity, CreateWalletLinkActivity::class.java)
            intent.putExtra(EXTRAS_BUNDLE, bundle)
            intent.putExtra(EXTRAS_CLIENT_KEY, clientKey)
            intent.putExtra(EXTRAS_ENVIRONMENT, environment)
            return intent
        }
    }
}
