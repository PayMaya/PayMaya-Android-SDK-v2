package com.paymaya.sdk.android.checkout.internal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentContract
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentPresenter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

internal class CheckoutActivity : PayMayaPaymentActivity<CheckoutRequest>() {

    override fun buildPresenter(
        environment: PayMayaEnvironment,
        clientKey: String
    ): PayMayaPaymentContract.Presenter<CheckoutRequest> {
        val json = Json(JsonConfiguration.Stable)
        // TODO JIRA PS-16 http logging level
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        val sendRequestUseCase = CheckoutUseCase(
            json,
            CheckoutRepository(environment, clientKey, json, httpClient)
        )
        return PayMayaPaymentPresenter(sendRequestUseCase)
    }

    companion object {
        fun newIntent(
            activity: Activity,
            requestData: CheckoutRequest,
            clientKey: String,
            environment: PayMayaEnvironment
        ): Intent {
            val bundle = Bundle()
            bundle.putParcelable(EXTRAS_REQUEST_DATA, requestData)
            val intent = Intent(activity, CheckoutActivity::class.java)
            intent.putExtra(EXTRAS_BUNDLE, bundle)
            intent.putExtra(EXTRAS_CLIENT_KEY, clientKey)
            intent.putExtra(EXTRAS_ENVIRONMENT, environment)
            return intent
        }
    }
}
