package com.paymaya.sdk.android.paywithpaymaya

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paymaya.sdk.android.common.exceptions.InternalException
import com.paymaya.sdk.android.common.exceptions.PaymentFailedException
import com.paymaya.sdk.android.common.internal.ErrorResponse
import com.paymaya.sdk.android.common.internal.SuccessResponse
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentContract
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentPresenter
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.paywithpaymaya.internal.SendSinglePaymentRequestUseCase
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
    private lateinit var sendSinglePaymentRequestUseCase: SendSinglePaymentRequestUseCase

    @Mock
    private lateinit var view: PayMayaPaymentContract.View

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        MockitoAnnotations.initMocks(this)

        json = Json(JsonConfiguration.Stable)
        presenter = PayMayaPaymentPresenter(sendSinglePaymentRequestUseCase)
    }

    @Test
    fun success() {
        runBlocking {
            whenever(sendSinglePaymentRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    responseId = CHECKOUT_ID,
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
            whenever(sendSinglePaymentRequestUseCase.run(any())).thenReturn(
                ErrorResponse(InternalException("Internal exception"))
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())

            verify(view).finishFailure(resultId = anyOrNull(), exception = any())
        }
    }

    @Test
    fun cancel() {
        runBlocking {
            whenever(sendSinglePaymentRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    responseId = CHECKOUT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.backButtonPressed()

            verify(view).finishCanceled(CHECKOUT_ID)
        }
    }

    @Test
    fun cancelUninitialized() {
        runBlocking {
            whenever(sendSinglePaymentRequestUseCase.run(any())).thenReturn(
                ErrorResponse(UnknownHostException())
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.backButtonPressed()

            verify(view).finishCanceled(null)
        }
    }

    @Test
    fun urlRedirectionSuccess() {
        runBlocking {
            whenever(sendSinglePaymentRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    responseId = CHECKOUT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_SUCCESS?someParameter=123")

            verify(view).finishSuccess(CHECKOUT_ID)
        }
    }

    @Test
    fun urlRedirectionCancel() {
        runBlocking {
            whenever(sendSinglePaymentRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    responseId = CHECKOUT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_CANCEL?someParameter=123")

            verify(view).finishCanceled(CHECKOUT_ID)
        }
    }

    @Test
    fun urlRedirectionFailure() {
        runBlocking {
            whenever(sendSinglePaymentRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    responseId = CHECKOUT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareSinglePaymentRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_FAILURE?someParameter=123")

            verify(view).finishFailure(CHECKOUT_ID, PaymentFailedException)
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
        private const val CHECKOUT_ID = "SAMPLE CHECKOUT ID"
        private const val REDIRECT_CHECKOUT_URL = "http://paymaya.com"
        private const val REDIRECT_URL_SUCCESS = "http://success.com"
        private const val REDIRECT_URL_FAILURE = "http://failure.com"
        private const val REDIRECT_URL_CANCEL = "http://cancel.com"
    }
}