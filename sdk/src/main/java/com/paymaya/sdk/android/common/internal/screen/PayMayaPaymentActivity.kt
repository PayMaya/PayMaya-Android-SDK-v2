package com.paymaya.sdk.android.common.internal.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.paymaya.sdk.android.BuildConfig
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.models.PayMayaRequest
import kotlinx.android.synthetic.main.paymaya_payment_activity.*

internal abstract class PayMayaPaymentActivity<R : PayMayaRequest> : Activity(),
    PayMayaPaymentContract.View {

    private lateinit var presenter: PayMayaPaymentContract.Presenter<R>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.paymaya.sdk.android.R.layout.paymaya_payment_activity)

        val intent = requireNotNull(intent)
        val bundle = requireNotNull(intent.getBundleExtra(EXTRAS_BUNDLE))
        val requestModel: R = requireNotNull(bundle.getParcelable(EXTRAS_REQUEST_DATA))
        val clientKey = requireNotNull(intent.getStringExtra(EXTRAS_CLIENT_KEY))
        val environment = requireNotNull(intent.getSerializableExtra(EXTRAS_ENVIRONMENT) as PayMayaEnvironment)

        initializeView()
        presenter = buildPresenter(environment, clientKey)

        presenter.viewCreated(this, requestModel)
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }

    abstract fun buildPresenter(
        environment: PayMayaEnvironment,
        clientKey: String
    ): PayMayaPaymentContract.Presenter<R>

    override fun onBackPressed() {
        presenter.backButtonPressed()
    }

    private fun initializeView() {
        CookieManager.getInstance().setAcceptThirdPartyCookies(paymayaPaymentActivityWebView, true)
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        @SuppressLint("SetJavaScriptEnabled")
        paymayaPaymentActivityWebView.settings.javaScriptEnabled = true
        paymayaPaymentActivityWebView.settings.allowFileAccess = true
        paymayaPaymentActivityWebView.webViewClient = WebViewClientImpl()
    }

    override fun loadUrl(redirectUrl: String) {
        paymayaPaymentActivityWebView.loadUrl(redirectUrl)
    }

    override fun finishSuccess(resultId: String) {
        val intent = Intent()
        intent.putExtra(EXTRAS_RESULT_ID, resultId)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun finishCanceled(resultId: String?) {
        val intent = Intent()
        resultId?.let { intent.putExtra(EXTRAS_RESULT_ID, it) }
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun finishFailure(resultId: String?, exception: Exception) {
        if (exception is kotlinx.coroutines.CancellationException) {
            finishCanceled(resultId)
            return
        }

        val intent = Intent()
        resultId?.let { intent.putExtra(EXTRAS_RESULT_ID, it) }
        intent.putExtra(EXTRAS_FAILURE_EXCEPTION, exception)
        setResult(RESULT_FAILURE, intent)
        finish()
    }

    private fun hideProgress() {
        paymayaPaymentActivityProgressBar.visibility = View.GONE
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
        const val EXTRAS_CLIENT_KEY = "EXTRAS_CLIENT_KEY"
        const val EXTRAS_ENVIRONMENT = "EXTRAS_ENVIRONMENT"
        const val EXTRAS_REQUEST_DATA = "EXTRAS_REQUEST_DATA"
        const val EXTRAS_BUNDLE = "EXTRAS_BUNDLE"

        const val RESULT_FAILURE = 1063
        const val EXTRAS_FAILURE_EXCEPTION = "EXTRAS_FAILURE_EXCEPTION"
        const val EXTRAS_RESULT_ID = "EXTRAS_RESULT_ID"
    }
}
