package com.paymaya.sdk.android.checkout.internal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.paymaya.sdk.android.BuildConfig
import com.paymaya.sdk.android.PayMayaEnvironment
import com.paymaya.sdk.android.checkout.models.Checkout
import com.paymaya.sdk.android.internal.PayMayaGatewayRepository
import kotlinx.android.synthetic.main.paymaya_checkout_activity.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

internal class PayMayaCheckoutActivity : Activity(), PayMayaCheckoutContract.View {

    private lateinit var presenter: PayMayaCheckoutContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.paymaya.sdk.android.R.layout.paymaya_checkout_activity)

        val intent = requireNotNull(intent)
        val bundle = requireNotNull(intent.getBundleExtra(EXTRAS_CHECKOUT_BUNDLE))
        val checkoutModel: Checkout = requireNotNull(bundle.getParcelable(EXTRAS_CHECKOUT))
        val clientKey = requireNotNull(intent.getStringExtra(EXTRAS_CLIENT_KEY))
        val environment = requireNotNull(intent.getSerializableExtra(EXTRAS_ENVIRONMENT) as PayMayaEnvironment)

        initializeView()
        buildPresenter(environment, clientKey)

        presenter.viewCreated(this, checkoutModel)
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }

    private fun buildPresenter(environment: PayMayaEnvironment, clientKey: String) {
        val json = Json(JsonConfiguration.Stable)
        // TODO JIRA PS-16 http logging level
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        presenter = PayMayaCheckoutPresenter(
            SendCheckoutRequestUseCase(
                json,
                PayMayaGatewayRepository(environment, clientKey, json, httpClient)
            )
        )
    }

    override fun onBackPressed() {
        presenter.backButtonPressed()
    }

    private fun initializeView() {
        CookieManager.getInstance().setAcceptThirdPartyCookies(paymayaCheckoutActivityWebView, true)
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        @SuppressLint("SetJavaScriptEnabled")
        paymayaCheckoutActivityWebView.settings.javaScriptEnabled = true
        paymayaCheckoutActivityWebView.settings.allowFileAccess = true
        paymayaCheckoutActivityWebView.webViewClient = WebViewClientImpl()
    }

    override fun loadUrl(redirectUrl: String) {
        paymayaCheckoutActivityWebView.loadUrl(redirectUrl)
    }

    override fun finishSuccess(checkoutId: String) {
        val intent = Intent()
        intent.putExtra(EXTRAS_CHECKOUT_ID, checkoutId)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun finishCanceled(checkoutId: String?) {
        val intent = Intent()
        checkoutId?.let { intent.putExtra(EXTRAS_CHECKOUT_ID, checkoutId) }
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun finishFailure(checkoutId: String?, exception: Exception) {
        val intent = Intent()
        checkoutId?.let { intent.putExtra(EXTRAS_CHECKOUT_ID, checkoutId) }
        intent.putExtra(EXTRAS_FAILURE_EXCEPTION, exception)
        setResult(RESULT_FAILURE, intent)
        finish()
    }

    private fun hideProgress() {
        paymayaCheckoutActivityProgressBar.visibility = View.GONE
    }

    inner class WebViewClientImpl : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
            presenter.urlBeingLoaded(url)

        override fun onPageFinished(view: WebView, url: String) {
            hideProgress()
            super.onPageFinished(view, url)
        }
    }

    companion object {
        private const val EXTRAS_CLIENT_KEY = "EXTRAS_CLIENT_KEY"
        private const val EXTRAS_ENVIRONMENT = "EXTRAS_ENVIRONMENT"
        private const val EXTRAS_CHECKOUT = "EXTRAS_CHECKOUT"
        private const val EXTRAS_CHECKOUT_BUNDLE = "EXTRAS_CHECKOUT_BUNDLE"

        const val RESULT_FAILURE = 1063
        const val EXTRAS_FAILURE_EXCEPTION = "EXTRAS_FAILURE_EXCEPTION"
        const val EXTRAS_CHECKOUT_ID = "EXTRAS_CHECKOUT_ID"

        fun newIntent(
            activity: Activity,
            checkout: Checkout,
            clientKey: String,
            environment: PayMayaEnvironment
        ): Intent {
            val checkoutBundle = Bundle()
            checkoutBundle.putParcelable(EXTRAS_CHECKOUT, checkout)
            val intent = Intent(activity, PayMayaCheckoutActivity::class.java)
            intent.putExtra(EXTRAS_CHECKOUT_BUNDLE, checkoutBundle)
            intent.putExtra(EXTRAS_CLIENT_KEY, clientKey)
            intent.putExtra(EXTRAS_ENVIRONMENT, environment)
            return intent
        }
    }
}
