package com.paymaya.sdk.android.vault

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.internal.Logger
import com.paymaya.sdk.android.vault.internal.CardInfoValidator
import com.paymaya.sdk.android.vault.internal.CardTypeDetector
import com.paymaya.sdk.android.vault.internal.TokenizeCardSuccessResponseWrapper
import com.paymaya.sdk.android.vault.internal.TokenizeCardUseCase
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import com.paymaya.sdk.android.vault.internal.screen.TokenizeCardContract
import com.paymaya.sdk.android.vault.internal.screen.TokenizeCardPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*

class TokenizeCardPresenterTest {

    private lateinit var presenter: TokenizeCardContract.Presenter

    @Mock
    private lateinit var tokenizeCardUseCase: TokenizeCardUseCase

    @Mock
    private lateinit var view: TokenizeCardContract.View

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        MockitoAnnotations.initMocks(this)

        val someDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, YEAR_CURRENT)
        }
        presenter = TokenizeCardPresenter(
            tokenizeCardUseCase,
            CardInfoValidator(someDate),
            CardTypeDetector(),
            Logger(LogLevel.WARN)
        )
    }

    @Test
    fun `pay button clicked - invalid card data`() {
        presenter.viewCreated(view)

        runBlocking {
            presenter.payButtonClicked(
                cardNumber = "1234",
                cardExpirationMonth = "44",
                cardExpirationYear = "1",
                cardCvc = "12"
            )
            verify(view).showCardNumberError()
            verify(view).showCardExpirationMonthError()
            verify(view).showCardExpirationYearError()
            verify(view).showCardCvcError()
        }
    }

    @Test
    fun `pay button clicked - invalid card data - empty fields`() {
        presenter.viewCreated(view)

        runBlocking {
            presenter.payButtonClicked(
                cardNumber = "",
                cardExpirationMonth = "",
                cardExpirationYear = "",
                cardCvc = ""
            )
            verify(view).showCardNumberError()
            verify(view).showCardExpirationMonthError()
            verify(view).showCardExpirationYearError()
            verify(view).showCardCvcError()
        }
    }

    @Test
    fun `pay button clicked - card data OK`() {
        presenter.viewCreated(view)

        runBlocking {

            whenever(tokenizeCardUseCase.run(any())).thenReturn(
                TokenizeCardSuccessResponseWrapper(
                    TokenizeCardResponse(
                        paymentTokenId = "",
                        state = "",
                        createdAt = "",
                        updatedAt = "",
                        issuer = ""
                    )
                )
            )

            presenter.payButtonClicked(
                CARD_NUMBER_VALID, "12", YEAR_FUTURE_NO_PREFIX, "123"
            )

            // just check, if progress bar is displayed and request is sent
            verify(view).showProgressBar()
            verify(tokenizeCardUseCase).run(any())
            verify(view).hideProgressBar()
            verify(view).finishSuccess(any())
        }
    }

    @Test
    fun `correct card detection after card number changed`(){
        val order = Mockito.inOrder(view)
        presenter.viewCreated(view)

        presenter.cardNumberChanged("3")
        presenter.cardNumberChanged("37")
        presenter.cardNumberChanged("3")
        presenter.cardNumberChanged("")
        presenter.cardNumberChanged("2")
        presenter.cardNumberChanged("21")
        presenter.cardNumberChanged("222")

        order.verify(view).showJcbMark()
        order.verify(view).showAmexMark()
        order.verify(view).showJcbMark()
        order.verify(view).hideCardMark()
        order.verify(view).showMcMark()
        order.verify(view).hideCardMark()
        order.verify(view).showMcMark()
    }

    companion object {
        private const val YEAR_CURRENT = 2020
        private const val YEAR_FUTURE_NO_PREFIX = "30"
        private const val CARD_NUMBER_VALID = "1234123412341238"
    }
}