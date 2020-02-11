package com.paymaya.sdk.android.paywithpaymaya.internal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentContract
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentPresenter
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

internal class SinglePaymentActivity : PayMayaPaymentActivity<SinglePaymentRequest>() {

    override fun buildPresenter(
        environment: PayMayaEnvironment,
        clientKey: String
    ): PayMayaPaymentContract.Presenter<SinglePaymentRequest> {
        val json = Json(JsonConfiguration.Stable)
        // TODO JIRA PS-16 http logging level
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        return PayMayaPaymentPresenter(
            SendSinglePaymentRequestUseCase(
                json,
                PayWithPayMayaRepository(environment, clientKey, json, httpClient)
            )
        )
    }

    companion object {
        fun newIntent(
            activity: Activity,
            requestData: SinglePaymentRequest,
            clientKey: String,
            environment: PayMayaEnvironment
        ): Intent {
            val bundle = Bundle()
            bundle.putParcelable(EXTRAS_REQUEST_DATA, requestData)
            val intent = Intent(activity, SinglePaymentActivity::class.java)
            intent.putExtra(EXTRAS_BUNDLE, bundle)
            intent.putExtra(EXTRAS_CLIENT_KEY, clientKey)
            intent.putExtra(EXTRAS_ENVIRONMENT, environment)
            return intent
        }
    }
}
