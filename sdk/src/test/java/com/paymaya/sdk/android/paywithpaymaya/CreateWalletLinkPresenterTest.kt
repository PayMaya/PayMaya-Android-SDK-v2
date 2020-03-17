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

package com.paymaya.sdk.android.paywithpaymaya

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PaymentStatus
import com.paymaya.sdk.android.common.exceptions.InternalException
import com.paymaya.sdk.android.common.exceptions.PaymentFailedException
import com.paymaya.sdk.android.common.internal.*
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentContract
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentPresenter
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.paywithpaymaya.internal.CreateWalletLinkUseCase
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.net.UnknownHostException

class CreateWalletLinkPresenterTest {

    private lateinit var presenter: PayMayaPaymentContract.Presenter<CreateWalletLinkRequest>

    @Mock
    private lateinit var createWalletLinkUseCase: CreateWalletLinkUseCase

    @Mock
    private lateinit var checkStatusUseCase: CheckStatusUseCase

    @Mock
    private lateinit var view: PayMayaPaymentContract.View

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        MockitoAnnotations.initMocks(this)

        presenter = PayMayaPaymentPresenter(createWalletLinkUseCase, checkStatusUseCase, Logger(LogLevel.DEBUG))
    }

    @Test
    fun success() {
        runBlocking {
            whenever(createWalletLinkUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = LINK_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareCreateWalletLinkRequest())

            verify(view).loadUrl(any())
        }
    }

    @Test
    fun failure() {
        runBlocking {
            whenever(createWalletLinkUseCase.run(any())).thenReturn(
                ErrorResponseWrapper(InternalException("Internal exception"))
            )
            presenter.viewCreated(view, prepareCreateWalletLinkRequest())

            verify(view).finishFailure(resultId = anyOrNull(), exception = any())
        }
    }

    @Test
    fun cancel() {
        runBlocking {
            whenever(createWalletLinkUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = LINK_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            whenever(checkStatusUseCase.run(any())).thenReturn(
                StatusSuccessResponseWrapper(
                    id = LINK_ID,
                    status = PaymentStatus.PAYMENT_EXPIRED
                )
            )
            presenter.viewCreated(view, prepareCreateWalletLinkRequest())
            presenter.backButtonPressed()

            verify(view).finishCanceled(LINK_ID)
        }
    }

    @Test
    fun cancelUninitialized() {
        runBlocking {
            whenever(createWalletLinkUseCase.run(any())).thenReturn(
                ErrorResponseWrapper(UnknownHostException())
            )
            presenter.viewCreated(view, prepareCreateWalletLinkRequest())
            presenter.backButtonPressed()

            verify(view).finishCanceled(resultId = null)
        }
    }

    @Test
    fun urlRedirectionSuccess() {
        runBlocking {
            whenever(createWalletLinkUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = LINK_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareCreateWalletLinkRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_SUCCESS?someParameter=123")

            verify(view).finishSuccess(LINK_ID)
        }
    }

    @Test
    fun urlRedirectionCancel() {
        runBlocking {
            whenever(createWalletLinkUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = LINK_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareCreateWalletLinkRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_CANCEL?someParameter=123")

            verify(view).finishCanceled(LINK_ID)
        }
    }

    @Test
    fun urlRedirectionFailure() {
        runBlocking {
            whenever(createWalletLinkUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = LINK_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareCreateWalletLinkRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_FAILURE?someParameter=123")

            verify(view).finishFailure(LINK_ID, PaymentFailedException)
        }
    }

    private fun prepareCreateWalletLinkRequest() =
        CreateWalletLinkRequest(
            requestReferenceNumber = "REQ_1",
            redirectUrl = RedirectUrl(
                success = REDIRECT_URL_SUCCESS,
                failure = REDIRECT_URL_FAILURE,
                cancel = REDIRECT_URL_CANCEL
            )
        )

    companion object {
        private const val LINK_ID = "SAMPLE LINK ID"
        private const val REDIRECT_CHECKOUT_URL = "http://paymaya.com"
        private const val REDIRECT_URL_SUCCESS = "http://success.com"
        private const val REDIRECT_URL_FAILURE = "http://failure.com"
        private const val REDIRECT_URL_CANCEL = "http://cancel.com"
    }
}