package com.paymaya.sdk.android.paywithpaymaya

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paymaya.sdk.android.common.exceptions.InternalException
import com.paymaya.sdk.android.common.exceptions.PaymentFailedException
import com.paymaya.sdk.android.common.internal.ErrorResponseWrapper
import com.paymaya.sdk.android.common.internal.RedirectSuccessResponseWrapper
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
    private lateinit var view: PayMayaPaymentContract.View

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        MockitoAnnotations.initMocks(this)

        presenter = PayMayaPaymentPresenter(createWalletLinkUseCase)
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

            verify(view).finishCanceled(null)
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