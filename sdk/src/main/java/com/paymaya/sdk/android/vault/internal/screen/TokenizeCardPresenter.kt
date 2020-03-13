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

package com.paymaya.sdk.android.vault.internal.screen

import com.paymaya.sdk.android.R
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.internal.AndroidString
import com.paymaya.sdk.android.common.internal.Constants.TAG
import com.paymaya.sdk.android.common.internal.ErrorResponseWrapper
import com.paymaya.sdk.android.common.internal.Logger
import com.paymaya.sdk.android.common.internal.ResponseWrapper
import com.paymaya.sdk.android.common.models.BaseError
import com.paymaya.sdk.android.common.models.GenericError
import com.paymaya.sdk.android.common.models.PaymentError
import com.paymaya.sdk.android.vault.internal.TokenizeCardSuccessResponseWrapper
import com.paymaya.sdk.android.vault.internal.TokenizeCardUseCase
import com.paymaya.sdk.android.vault.internal.helpers.CardInfoValidator
import com.paymaya.sdk.android.vault.internal.helpers.CardType
import com.paymaya.sdk.android.vault.internal.helpers.CardTypeDetector
import com.paymaya.sdk.android.vault.internal.models.Card
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardRequest
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

internal class TokenizeCardPresenter(
    private val tokenizeCardUseCase: TokenizeCardUseCase,
    private val cardInfoValidator: CardInfoValidator,
    private val cardTypeDetector: CardTypeDetector,
    private val logger: Logger
) : TokenizeCardContract.Presenter, CoroutineScope {

    private val job: Job = Job()
    private var view: TokenizeCardContract.View? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun viewCreated(view: TokenizeCardContract.View) {
        this.view = view
    }

    override fun viewDestroyed() {
        job.cancel()
        this.view = null
    }

    override fun backButtonPressed() {
        job.cancel()
        view?.finishCanceled()
    }

    override fun payButtonClicked(
        cardNumberWithSpaces: String,
        cardExpirationDate: String,
        cardCvc: String
    ) {
        view?.hideKeyboard()

        if (!validateCardInfo(
                cardNumberWithSpaces = cardNumberWithSpaces,
                cardExpirationDate = cardExpirationDate,
                cardCvc = cardCvc
            )
        ) return

        val cardExpirationMonth = getMonth(cardExpirationDate)
        val cardExpirationYear = getYear(cardExpirationDate)

        val card = prepareCardModel(
            cardNumberWithSpaces = cardNumberWithSpaces,
            cardExpirationMonth = cardExpirationMonth,
            cardExpirationYear = cardExpirationYear,
            cardCvc = cardCvc
        )

        val request = TokenizeCardRequest(card)

        launch {
            view?.showProgressBar()
            val response = tokenizeCardUseCase.run(request)
            view?.hideProgressBar()
            processResponse(response)
        }
    }

    private fun validateCardInfo(
        cardNumberWithSpaces: String,
        cardExpirationDate: String,
        cardCvc: String
    ): Boolean {
        var valid = true
        valid = checkCardNumber(cardNumberWithSpaces) && valid
        valid = checkCardExpirationDate(cardExpirationDate) && valid
        valid = checkCardCvc(cardCvc) && valid

        return valid
    }

    private fun checkCardNumber(value: String): Boolean =
        cardInfoValidator
            .validateNumber(removeSpaces(value))
            .also { valid ->
                if (valid) view?.hideCardNumberError() else view?.showCardNumberError()
            }

    private fun checkCardExpirationDate(cardExpirationDate: String): Boolean {
        if (!checkDateFormat(cardExpirationDate)) {
            return false
        }

        val cardExpirationMonth = getMonth(cardExpirationDate)
        if (!checkCardExpirationMonth(cardExpirationMonth)) {
            return false
        }

        val cardExpirationYear = getYear(cardExpirationDate)
        if (!checkCardExpirationYear(cardExpirationYear)) {
            return false
        }

        return checkDateIsInFuture(cardExpirationMonth, cardExpirationYear)
    }

    private fun checkDateFormat(cardExpirationDate: String): Boolean =
        cardInfoValidator
            .validateDateFormat(cardExpirationDate)
            .also { valid ->
                if (!valid) view?.showCardExpirationDateError()
            }

    private fun checkCardExpirationMonth(value: String): Boolean =
        cardInfoValidator
            .validateMonth(value)
            .also { valid ->
                if (!valid) view?.showCardExpirationDateError()
            }

    private fun checkCardExpirationYear(value: String): Boolean =
        cardInfoValidator
            .validateYear(value)
            .also { valid ->
                if (!valid) view?.showCardExpirationDateError()
            }

    private fun checkDateIsInFuture(cardExpirationMonth: String, cardExpirationYear: String): Boolean =
        cardInfoValidator
            .validateFutureDate(cardExpirationMonth, formatYear(cardExpirationYear))
            .also { valid ->
                if (valid) view?.hideCardExpirationDateError() else view?.showCardExpirationDateError()
            }

    private fun checkCardCvc(value: String): Boolean =
        cardInfoValidator
            .validateCvc(value)
            .also { valid ->
                if (valid) view?.hideCardCvcError() else view?.showCardCvcError()
            }

    override fun cardNumberChanged(cardNumber: String) {
        view?.hideCardNumberError()
        view?.hideCardCvcHint()
        val cardType = cardTypeDetector.detectType(cardNumber)
        showCardIcon(cardType)
    }

    private fun showCardIcon(value: CardType) {
        when (value) {
            CardType.VISA -> view?.showCardIcon(R.drawable.visa)
            CardType.MASTER_CARD -> view?.showCardIcon(R.drawable.mastercard)
            CardType.JCB -> view?.showCardIcon(R.drawable.jcb)
            CardType.AMEX -> view?.showCardIcon(R.drawable.amex)
            CardType.UNKNOWN -> view?.hideCardIcon()
        }
    }

    override fun cardExpirationDateChanged() {
        view?.hideCardExpirationDateError()
        view?.hideCardCvcHint()
    }

    override fun cardCvcChanged() {
        view?.hideCardCvcError()
        view?.hideCardCvcHint()
    }

    override fun cardNumberFocusLost(value: String) {
        checkCardNumber(value)
    }

    override fun cardExpirationDateFocusReceived() {
        view?.showCardExpirationDateHint()
    }

    override fun cardExpirationDateFocusLost(value: String) {
        checkCardExpirationDate(value)
    }

    override fun cardCvcFocusLost(value: String) {
        checkCardCvc(value)
    }

    private fun prepareCardModel(
        cardNumberWithSpaces: String,
        cardExpirationMonth: String,
        cardExpirationYear: String,
        cardCvc: String
    ): Card =
        Card(
            number = removeSpaces(cardNumberWithSpaces),
            expMonth = cardExpirationMonth,
            expYear = formatYear(cardExpirationYear),
            cvc = cardCvc
        )

    private fun removeSpaces(text: String): String =
        text.replace(oldValue = " ", newValue = "")

    private fun formatYear(cardExpirationYear: String): String =
        "$YEAR_PREFIX$cardExpirationYear"

    private fun getMonth(cardExpirationDate: String): String =
        cardExpirationDate.substring(0..1)

    private fun getYear(cardExpirationDate: String): String =
        cardExpirationDate.substring(3..4)

    private fun processResponse(responseWrapper: ResponseWrapper) {
        when (responseWrapper) {
            is TokenizeCardSuccessResponseWrapper -> processSuccessResponse(responseWrapper)
            is ErrorResponseWrapper -> processErrorResponse(responseWrapper)
            else -> throw IllegalStateException(responseWrapper.toString())
        }
    }

    private fun processSuccessResponse(vaultTokenizeCardSuccessResponse: TokenizeCardSuccessResponseWrapper) {
        val result = TokenizeCardResponse(
            paymentTokenId = vaultTokenizeCardSuccessResponse.response.paymentTokenId,
            state = vaultTokenizeCardSuccessResponse.response.state,
            createdAt = vaultTokenizeCardSuccessResponse.response.createdAt,
            updatedAt = vaultTokenizeCardSuccessResponse.response.updatedAt,
            issuer = vaultTokenizeCardSuccessResponse.response.issuer
        )
        view?.finishSuccess(result)
    }

    private fun processErrorResponse(responseWrapper: ErrorResponseWrapper) {
        val exception = responseWrapper.exception
        if (exception is kotlinx.coroutines.CancellationException) {
            view?.finishCanceled()
            return
        }

        val message = getExceptionMessage(exception)
        view?.showErrorPopup(message)
    }

    private fun getExceptionMessage(exception: Exception): AndroidString =
        when (exception) {
            is BadRequestException -> getMessageBadRequestMessage(exception.error)
            is UnknownHostException -> AndroidString(R.string.paymaya_connection_error)
            else -> {
                logger.e(TAG, "Unknown error: ${exception.javaClass.simpleName}")
                AndroidString(R.string.paymaya_unknown_error)
            }
        }

    private fun getMessageBadRequestMessage(baseError: BaseError): AndroidString =
        when (baseError) {
            is GenericError -> AndroidString(baseError.error)
            is PaymentError -> AndroidString(getPaymentErrorMessage(baseError))
            else -> {
                logger.e(TAG, "Unknown error: ${baseError.javaClass.simpleName}")
                AndroidString(R.string.paymaya_unknown_error)
            }
        }

    private fun getPaymentErrorMessage(paymentError: PaymentError): String {
        var result = paymentError.message
        if (!paymentError.parameters.isNullOrEmpty()) {
            result += "\n"
            paymentError.parameters.forEach {
                result += "\n● ${it.description}"
            }
        }
        return result
    }

    override fun cardCvcInfoClicked() {
        view?.showCardCvcHint()
    }

    override fun screenMaskClicked() {
        view?.hideCardCvcHint()
    }

    companion object {
        private const val YEAR_PREFIX = "20"
    }
}
