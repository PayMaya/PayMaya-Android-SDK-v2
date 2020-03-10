package com.paymaya.sdk.android.common.internal.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.*
import com.paymaya.sdk.android.BuildConfig
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.models.PayMayaRequest
import kotlinx.android.synthetic.main.activity_paymaya_payment.*

internal abstract class PayMayaPaymentActivity<R : PayMayaRequest> :
    Activity(), PayMayaPaymentContract.View {

    private lateinit var presenter: PayMayaPaymentContract.Presenter<R>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.paymaya.sdk.android.R.layout.activity_paymaya_payment)

        val intent = requireNotNull(intent)
        val bundle = requireNotNull(intent.getBundleExtra(EXTRAS_BUNDLE))
        val requestModel: R = requireNotNull(bundle.getParcelable(EXTRAS_REQUEST_DATA))
        val clientKey = requireNotNull(intent.getStringExtra(EXTRAS_CLIENT_KEY))
        val environment = requireNotNull(intent.getSerializableExtra(EXTRAS_ENVIRONMENT) as PayMayaEnvironment)
        val logLevel = requireNotNull(intent.getSerializableExtra(EXTRAS_LOG_LEVEL) as LogLevel)

        initializeView()

        presenter = buildPresenter(environment, clientKey, logLevel)
        presenter.viewCreated(this, requestModel)
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }

    protected abstract fun buildPresenter(
        environment: PayMayaEnvironment,
        clientKey: String,
        logLevel: LogLevel
    ): PayMayaPaymentContract.Presenter<R>

    override fun onBackPressed() {
        presenter.backButtonPressed()
    }

    private fun initializeView() {
        CookieManager.getInstance().setAcceptThirdPartyCookies(payMayaPaymentActivityWebView, true)
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        @SuppressLint("SetJavaScriptEnabled")
        payMayaPaymentActivityWebView.settings.javaScriptEnabled = true
        payMayaPaymentActivityWebView.settings.allowFileAccess = true
        payMayaPaymentActivityWebView.webViewClient = WebViewClientImpl()
    }

    override fun loadUrl(redirectUrl: String) {
        payMayaPaymentActivityWebView.loadUrl(redirectUrl)
    }

    override fun showNoConnectionScreen() {
        payMayaNoConnectionScreen.visibility = View.VISIBLE
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
        val intent = Intent()
        resultId?.let { intent.putExtra(EXTRAS_RESULT_ID, it) }
        intent.putExtra(EXTRAS_FAILURE_EXCEPTION, exception)
        setResult(RESULT_FAILURE, intent)
        finish()
    }

    override fun showProgressBar() {
        payMayaPaymentActivityProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        payMayaPaymentActivityProgressBar.visibility = View.GONE
    }

    override fun hideWebView() {
        payMayaPaymentActivityWebView.visibility = View.GONE
        payMayaPaymentActivityWebView.webViewClient = NoOpWebViewClientImpl()

        // Checkout web page automatically sends Status requests, which we want to ignore.
        // Note: This relies on the implementation of the PayMaya’s Checkout web page.
        payMayaPaymentActivityWebView.stopLoading()
    }

    override fun showCheckingPaymentStatusLabel() {
        payMayaCheckingPaymentStatusLabel.visibility = View.VISIBLE
    }

    inner class WebViewClientImpl : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
            presenter.urlBeingLoaded(url)

        override fun onPageFinished(view: WebView, url: String) {
            hideProgressBar()
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            if (!request?.url.toString().endsWith(REQUEST_STATUS_SUFFIX)) {
                presenter.connectionLost()
            }
        }
    }

    class NoOpWebViewClientImpl : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            // no-op
            return true
        }
    }

    companion object {
        private const val REQUEST_STATUS_SUFFIX = "/status"

        const val EXTRAS_CLIENT_KEY = "EXTRAS_CLIENT_KEY"
        const val EXTRAS_ENVIRONMENT = "EXTRAS_ENVIRONMENT"
        const val EXTRAS_LOG_LEVEL = "EXTRAS_LOG_LEVEL"
        const val EXTRAS_REQUEST_DATA = "EXTRAS_REQUEST_DATA"
        const val EXTRAS_BUNDLE = "EXTRAS_BUNDLE"

        const val RESULT_FAILURE = 1063
        const val EXTRAS_FAILURE_EXCEPTION = "EXTRAS_FAILURE_EXCEPTION"
        const val EXTRAS_RESULT_ID = "EXTRAS_RESULT_ID"
    }
}
