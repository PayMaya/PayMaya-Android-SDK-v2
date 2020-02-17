package com.paymaya.sdk.android.checkout

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paymaya.sdk.android.checkout.internal.CheckoutUseCase
import com.paymaya.sdk.android.checkout.models.Buyer
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.common.exceptions.InternalException
import com.paymaya.sdk.android.common.exceptions.PaymentFailedException
import com.paymaya.sdk.android.common.internal.ErrorResponseWrapper
import com.paymaya.sdk.android.common.internal.RedirectSuccessResponseWrapper
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentContract
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentPresenter
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
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

    private lateinit var presenter: PayMayaPaymentContract.Presenter<CheckoutRequest>
    private lateinit var json: Json

    @Mock
    private lateinit var checkoutUseCase: CheckoutUseCase

    @Mock
    private lateinit var view: PayMayaPaymentContract.View

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        MockitoAnnotations.initMocks(this)

        json = Json(JsonConfiguration.Stable)
        presenter = PayMayaPaymentPresenter(checkoutUseCase)
    }

    @Test
    fun success() {
        runBlocking {
            whenever(checkoutUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = CHECKOUT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareCheckoutRequest())

            verify(view).loadUrl(any())
        }
    }

    @Test
    fun failure() {
        runBlocking {
            whenever(checkoutUseCase.run(any())).thenReturn(
                ErrorResponseWrapper(InternalException("Internal exception"))
            )
            presenter.viewCreated(view, prepareCheckoutRequest())

            verify(view).finishFailure(resultId = anyOrNull(), exception = any())
        }
    }

    @Test
    fun cancel() {
        runBlocking {
            whenever(checkoutUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = CHECKOUT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareCheckoutRequest())
            presenter.backButtonPressed()

            verify(view).finishCanceled(CHECKOUT_ID)
        }
    }

    @Test
    fun cancelUninitialized() {
        runBlocking {
            whenever(checkoutUseCase.run(any())).thenReturn(
                ErrorResponseWrapper(UnknownHostException())
            )
            presenter.viewCreated(view, prepareCheckoutRequest())
            presenter.backButtonPressed()

            verify(view).finishCanceled(null)
        }
    }

    @Test
    fun urlRedirectionSuccess() {
        runBlocking {
            whenever(checkoutUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = CHECKOUT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareCheckoutRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_SUCCESS?someParameter=123")

            verify(view).finishSuccess(CHECKOUT_ID)
        }
    }

    @Test
    fun urlRedirectionCancel() {
        runBlocking {
            whenever(checkoutUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = CHECKOUT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareCheckoutRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_CANCEL?someParameter=123")

            verify(view).finishCanceled(CHECKOUT_ID)
        }
    }

    @Test
    fun urlRedirectionFailure() {
        runBlocking {
            whenever(checkoutUseCase.run(any())).thenReturn(
                RedirectSuccessResponseWrapper(
                    resultId = CHECKOUT_ID,
                    redirectUrl = REDIRECT_CHECKOUT_URL
                )
            )
            presenter.viewCreated(view, prepareCheckoutRequest())
            presenter.urlBeingLoaded("$REDIRECT_URL_FAILURE?someParameter=123")

            verify(view).finishFailure(CHECKOUT_ID, PaymentFailedException)
        }
    }

    private fun prepareCheckoutRequest() =
        CheckoutRequest(
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
                    totalAmount = TotalAmount(
                        BigDecimal(10),
                        "PHP"
                    )
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