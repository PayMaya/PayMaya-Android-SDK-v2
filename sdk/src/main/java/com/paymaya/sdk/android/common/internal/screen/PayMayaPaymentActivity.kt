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

package com.paymaya.sdk.android.common.internal.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.paymaya.sdk.android.BuildConfig
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.models.PayMayaRequest
import com.paymaya.sdk.android.databinding.ActivityPaymayaPaymentBinding

internal abstract class PayMayaPaymentActivity<R : PayMayaRequest> :
    AppCompatActivity(), PayMayaPaymentContract.View {

    private lateinit var presenter: PayMayaPaymentContract.Presenter<R>
    private lateinit var binding: ActivityPaymayaPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = requireNotNull(intent)
        val bundle = requireNotNull(intent.getBundleExtra(EXTRAS_BUNDLE))
        val requestModel: R = requireNotNull(bundle.getParcelable(EXTRAS_REQUEST))
        val clientPublicKey = requireNotNull(intent.getStringExtra(EXTRAS_CLIENT_PUBLIC_KEY))
        val environment = requireNotNull(intent.getSerializableExtra(EXTRAS_ENVIRONMENT) as PayMayaEnvironment)
        val logLevel = requireNotNull(intent.getSerializableExtra(EXTRAS_LOG_LEVEL) as LogLevel)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                presenter.backButtonPressed()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        binding = ActivityPaymayaPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initializeView()

        presenter = buildPresenter(environment, clientPublicKey, logLevel)
        presenter.viewCreated(this, requestModel)
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }

    protected abstract fun buildPresenter(
        environment: PayMayaEnvironment,
        clientPublicKey: String,
        logLevel: LogLevel
    ): PayMayaPaymentContract.Presenter<R>

    private fun initializeView() {
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.payMayaPaymentActivityWebView, true)
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        @SuppressLint("SetJavaScriptEnabled")
        binding.payMayaPaymentActivityWebView.settings.javaScriptEnabled = true
        binding.payMayaPaymentActivityWebView.settings.allowFileAccess = true
        binding.payMayaPaymentActivityWebView.webViewClient = WebViewClientImpl()
    }

    override fun loadUrl(redirectUrl: String) {
        binding.payMayaPaymentActivityWebView.loadUrl(redirectUrl)
    }

    override fun showNoConnectionScreen() {
        binding.payMayaNoConnectionScreen.visibility = View.VISIBLE
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
        binding.payMayaPaymentActivityProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        binding.payMayaPaymentActivityProgressBar.visibility = View.GONE
    }

    override fun hideWebView() {
        binding.payMayaPaymentActivityWebView.visibility = View.GONE
        binding.payMayaPaymentActivityWebView.webViewClient = NoOpWebViewClientImpl()
        binding.payMayaPaymentActivityWebView.stopLoading()
    }

    override fun showCheckingPaymentStatusLabel() {
        binding.payMayaCheckingPaymentStatusLabel.visibility = View.VISIBLE
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

            // Checkout web page automatically sends Status requests, which we want to ignore.
            // Note: This relies on the implementation of the PayMaya’s Checkout web page.
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

        const val EXTRAS_CLIENT_PUBLIC_KEY = "EXTRAS_CLIENT_PUBLIC_KEY"
        const val EXTRAS_ENVIRONMENT = "EXTRAS_ENVIRONMENT"
        const val EXTRAS_LOG_LEVEL = "EXTRAS_LOG_LEVEL"
        const val EXTRAS_REQUEST = "EXTRAS_REQUEST"
        const val EXTRAS_BUNDLE = "EXTRAS_BUNDLE"

        const val RESULT_FAILURE = 1063
        const val EXTRAS_FAILURE_EXCEPTION = "EXTRAS_FAILURE_EXCEPTION"
        const val EXTRAS_RESULT_ID = "EXTRAS_RESULT_ID"
    }
}
