package com.paymaya.sdk.android

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paymaya.sdk.android.checkout.exceptions.InternalException
import com.paymaya.sdk.android.checkout.exceptions.PaymentFailedException
import com.paymaya.sdk.android.checkout.internal.*
import com.paymaya.sdk.android.checkout.models.*
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

class PayMayaCheckoutPresenterTest {

    private lateinit var presenter: PayMayaCheckoutContract.Presenter
    private lateinit var json: Json

    @Mock
    private lateinit var sendCheckoutRequestUseCase: SendCheckoutRequestUseCase

    @Mock
    private lateinit var view: PayMayaCheckoutContract.View

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        MockitoAnnotations.initMocks(this)

        json = Json(JsonConfiguration.Stable)
        presenter = PayMayaCheckoutPresenter(sendCheckoutRequestUseCase)
    }

    @Test
    fun success() {
        runBlocking {
            whenever(sendCheckoutRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    REDIRECT_CHECKOUT_URL,
                    CHECKOUT_ID
                )
            )
            presenter.viewCreated(view, prepareCheckoutModel())

            verify(view).loadUrl(any())
        }
    }

    @Test
    fun failure() {
        runBlocking {
            whenever(sendCheckoutRequestUseCase.run(any())).thenReturn(
                ErrorResponse(InternalException("Internal exception"))
            )
            presenter.viewCreated(view, prepareCheckoutModel())

            verify(view).finishFailure(checkoutId = anyOrNull(), exception = any())
        }
    }

    @Test
    fun cancel() {
        runBlocking {
            whenever(sendCheckoutRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    REDIRECT_CHECKOUT_URL,
                    CHECKOUT_ID
                )
            )
            presenter.viewCreated(view, prepareCheckoutModel())
            presenter.backButtonPressed()

            verify(view).finishCanceled(CHECKOUT_ID)
        }
    }

    @Test
    fun cancelUninitialized() {
        runBlocking {
            whenever(sendCheckoutRequestUseCase.run(any())).thenReturn(
                ErrorResponse(UnknownHostException())
            )
            presenter.viewCreated(view, prepareCheckoutModel())
            presenter.backButtonPressed()

            verify(view).finishCanceled(null)
        }
    }

    @Test
    fun urlRedirectionSuccess() {
        runBlocking {
            whenever(sendCheckoutRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    REDIRECT_CHECKOUT_URL,
                    CHECKOUT_ID
                )
            )
            presenter.viewCreated(view, prepareCheckoutModel())
            presenter.urlBeingLoaded("$REDIRECT_URL_SUCCESS?someParameter=123")

            verify(view).finishSuccess(CHECKOUT_ID)
        }
    }

    @Test
    fun urlRedirectionCancel() {
        runBlocking {
            whenever(sendCheckoutRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    REDIRECT_CHECKOUT_URL,
                    CHECKOUT_ID
                )
            )
            presenter.viewCreated(view, prepareCheckoutModel())
            presenter.urlBeingLoaded("$REDIRECT_URL_CANCEL?someParameter=123")

            verify(view).finishCanceled(CHECKOUT_ID)
        }
    }

    @Test
    fun urlRedirectionFailure() {
        runBlocking {
            whenever(sendCheckoutRequestUseCase.run(any())).thenReturn(
                SuccessResponse(
                    REDIRECT_CHECKOUT_URL,
                    CHECKOUT_ID
                )
            )
            presenter.viewCreated(view, prepareCheckoutModel())
            presenter.urlBeingLoaded("$REDIRECT_URL_FAILURE?someParameter=123")

            verify(view).finishFailure(CHECKOUT_ID, PaymentFailedException)
        }
    }

    private fun prepareCheckoutModel() =
        Checkout(
            totalAmount = TotalAmount(
                value = BigDecimal(99999),
                currency = "PHP"
            ),
            buyer = Buyer(
                firstName = "John",
                lastName = "Doe"
            ),
            items = listOf(
                Item(
                    name = "shoes",
                    quantity = 1,
                    totalAmount = TotalAmount(BigDecimal(10), "PHP")
                )
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