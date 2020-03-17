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
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.paywithpaymaya.internal.SinglePaymentUseCase
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import java.net.UnknownHostException

class SinglePaymentPresenterTest {

    private lateinit var presenter: PayMayaPaymentContract.Presenter<SinglePaymentRequest>
    private lateinit var json: Json

    @Mock
    private lateinit var singlePaymentUseCase: SinglePaymentUseCase

    @Mock
    private lateinit var checkStatusUseCase: CheckStatusUseCase

    @Mock
    private lateinit var view: PayMayaPaymentContract.View

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        MockitoAnnotations.initMocks(this)

        json = Json(JsonConfiguration.Stable)
        presenter = PayMayaPaymentPresenter(singlePaymentUseCase, checkStatusUseCase, Logger(LogLevel.DEBUG))
    }

    @Test
    fun success() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = PAYMENT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())

            verify(view).loadUrl(any())
        }
    }

    @Test
    fun failure() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                ErrorResponseWrapper(InternalException("Internal exception"))
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())

            verify(view).finishFailure(resultId = anyOrNull(), exception = any())
        }
    }

    @Test
    fun cancel() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = PAYMENT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            whenever(checkStatusUseCase.run(any())).thenReturn(
                StatusSuccessResponseWrapper(
                    id = PAYMENT_ID,
                    status = PaymentStatus.PAYMENT_EXPIRED
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.backButtonPressed()

            verify(view).finishCanceled(PAYMENT_ID)
        }
    }

    @Test
    fun `canceled but succeeded`() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = PAYMENT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            whenever(checkStatusUseCase.run(any())).thenReturn(
                StatusSuccessResponseWrapper(
                    id = PAYMENT_ID,
                    status = PaymentStatus.PAYMENT_SUCCESS
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.backButtonPressed()

            verify(view).finishSuccess(PAYMENT_ID)
        }
    }

    @Test
    fun `canceled but failed`() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = PAYMENT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            whenever(checkStatusUseCase.run(any())).thenReturn(
                StatusSuccessResponseWrapper(
                    id = PAYMENT_ID,
                    status = PaymentStatus.PAYMENT_FAILED
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.backButtonPressed()

            verify(view).finishFailure(PAYMENT_ID, PaymentFailedException)
        }
    }

    @Test
    fun `canceled but failed (auth)`() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = PAYMENT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            whenever(checkStatusUseCase.run(any())).thenReturn(
                StatusSuccessResponseWrapper(
                    id = PAYMENT_ID,
                    status = PaymentStatus.AUTH_FAILED
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.backButtonPressed()

            verify(view).finishFailure(PAYMENT_ID, PaymentFailedException)
        }
    }

    @Test
    fun cancelUninitialized() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                ErrorResponseWrapper(UnknownHostException())
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.backButtonPressed()

            verify(view).finishCanceled(resultId = null)
        }
    }

    @Test
    fun urlRedirectionSuccess() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = PAYMENT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_SUCCESS?someParameter=123")

            verify(view).finishSuccess(PAYMENT_ID)
        }
    }

    @Test
    fun urlRedirectionCancel() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = PAYMENT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_CANCEL?someParameter=123")

            verify(view).finishCanceled(PAYMENT_ID)
        }
    }

    @Test
    fun urlRedirectionFailure() {
        runBlocking {
            whenever(singlePaymentUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = PAYMENT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_FAILURE?someParameter=123")

            verify(view).finishFailure(PAYMENT_ID, PaymentFailedException)
        }
    }

    private fun prepareSinglePaymentRequest() =
        SinglePaymentRequest(
            totalAmount = TotalAmount(
                value = BigDecimal(99999),
                currency = "PHP"
            ),
            requestReferenceNumber = "REQ_1",
            redirectUrl = RedirectUrl(
                success = REDIRECT_URL_SUCCESS,
                failure = REDIRECT_URL_FAILURE,
                cancel = REDIRECT_URL_CANCEL
            )
        )

    companion object {
        private const val PAYMENT_ID = "SAMPLE PAYMENT ID"
        private const val REDIRECT_CHECKOUT_URL = "http://paymaya.com"
        private const val REDIRECT_URL_SUCCESS = "http://success.com"
        private const val REDIRECT_URL_FAILURE = "http://failure.com"
        private const val REDIRECT_URL_CANCEL = "http://cancel.com"
    }
}