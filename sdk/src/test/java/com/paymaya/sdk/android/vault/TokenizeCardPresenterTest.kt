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

package com.paymaya.sdk.android.vault

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paymaya.sdk.android.R
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.internal.Logger
import com.paymaya.sdk.android.vault.internal.helpers.CardTypeDetector
import com.paymaya.sdk.android.vault.internal.TokenizeCardSuccessResponseWrapper
import com.paymaya.sdk.android.vault.internal.TokenizeCardUseCase
import com.paymaya.sdk.android.vault.internal.helpers.CardInfoValidator
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
                cardNumberWithSpaces = "1234",
                cardExpirationDate = "44/1",
                cardCvc = "12"
            )
            verify(view).showCardNumberError()
            verify(view).showCardExpirationDateError()
            verify(view).showCardCvcError()
        }
    }

    @Test
    fun `pay button clicked - invalid card data - empty fields`() {
        presenter.viewCreated(view)

        runBlocking {
            presenter.payButtonClicked(
                cardNumberWithSpaces = "",
                cardExpirationDate = "",
                cardCvc = ""
            )
            verify(view).showCardNumberError()
            verify(view).showCardExpirationDateError()
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
                cardNumberWithSpaces = CARD_NUMBER_VALID,
                cardExpirationDate = "12/$YEAR_FUTURE_NO_PREFIX",
                cardCvc = "123"
            )

            // just check, if progress bar is displayed and request is sent
            verify(view).showProgressBar()
            verify(tokenizeCardUseCase).run(any())
            verify(view).hideProgressBar()
            verify(view).finishSuccess(any())
        }
    }

    @Test
    fun `correct card detection after card number changed`() {
        val order = Mockito.inOrder(view)
        presenter.viewCreated(view)

        presenter.cardNumberChanged("3")
        presenter.cardNumberChanged("35")
        presenter.cardNumberChanged("37")
        presenter.cardNumberChanged("")
        presenter.cardNumberChanged("2")
        presenter.cardNumberChanged("")
        presenter.cardNumberChanged("2620")

        order.verify(view).showCardIcon(R.drawable.amex)
        order.verify(view).showCardIcon(R.drawable.jcb)
        order.verify(view).showCardIcon(R.drawable.amex)
        order.verify(view).hideCardIcon()
        order.verify(view).showCardIcon(R.drawable.mastercard)
        order.verify(view).hideCardIcon()
        order.verify(view).showCardIcon(R.drawable.mastercard)
    }

    companion object {
        private const val YEAR_CURRENT = 2020
        private const val YEAR_FUTURE_NO_PREFIX = "30"
        private const val CARD_NUMBER_VALID = "1234123412341238"
    }
}