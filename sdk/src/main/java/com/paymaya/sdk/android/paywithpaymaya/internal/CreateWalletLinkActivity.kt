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
