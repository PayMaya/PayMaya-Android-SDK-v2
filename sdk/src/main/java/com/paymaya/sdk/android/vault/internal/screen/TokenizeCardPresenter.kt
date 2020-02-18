package com.paymaya.sdk.android.vault.internal.screen

import com.paymaya.sdk.android.R
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.internal.ErrorResponseWrapper
import com.paymaya.sdk.android.common.internal.Resource
import com.paymaya.sdk.android.common.internal.ResponseWrapper
import com.paymaya.sdk.android.common.models.BaseError
import com.paymaya.sdk.android.common.models.GenericError
import com.paymaya.sdk.android.common.models.PaymentError
import com.paymaya.sdk.android.vault.internal.CardInfoValidator
import com.paymaya.sdk.android.vault.internal.TokenizeCardSuccessResponseWrapper
import com.paymaya.sdk.android.vault.internal.TokenizeCardUseCase
import com.paymaya.sdk.android.vault.internal.models.Card
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardRequest
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.*
import kotlin.coroutines.CoroutineContext

internal class TokenizeCardPresenter(
    private val tokenizeCardUseCase: TokenizeCardUseCase,
    private val cardInfoValidator: CardInfoValidator
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
        cardNumber: String,
        cardExpirationMonth: String,
        cardExpirationYear: String,
        cardCvc: String
    ) {
        view?.hideKeyboard()

        if (!validateCardInfo(
                cardNumber = cardNumber,
                cardExpirationMonth = cardExpirationMonth,
                cardExpirationYear = cardExpirationYear,
                cardCvc = cardCvc
            )
        ) return

        val card = prepareCardModel(
            cardNumber = cardNumber,
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
        cardNumber: String,
        cardExpirationMonth: String,
        cardExpirationYear: String,
        cardCvc: String
    ): Boolean {
        var valid = true
        valid = checkCardNumber(cardNumber) && valid
        valid = checkCardExpirationMonth(cardExpirationMonth) && valid
        valid = checkCardExpirationYear(cardExpirationYear) && valid
        valid = checkCardCvc(cardCvc) && valid

        if (valid) {
            valid = checkCardExpirationDate(cardExpirationMonth, cardExpirationYear) && valid
        }

        return valid
    }

    private fun checkCardNumber(value: String): Boolean =
        cardInfoValidator
            .validateNumber(value)
            .also { valid ->
                if (valid) view?.hideCardNumberError() else view?.showCardNumberError()
            }

    private fun checkCardExpirationMonth(value: String): Boolean =
        cardInfoValidator
            .validateMonth(value)
            .also { valid ->
                if (valid) view?.hideCardExpirationMonthError() else view?.showCardExpirationMonthError()
            }

    private fun checkCardExpirationYear(value: String): Boolean =
        cardInfoValidator
            .validateYear(value)
            .also { valid ->
                if (valid) view?.hideCardExpirationYearError() else view?.showCardExpirationYearError()
            }

    private fun checkCardCvc(value: String): Boolean =
        cardInfoValidator
            .validateCvc(value)
            .also { valid ->
                if (valid) view?.hideCardCvcError() else view?.showCardCvcError()
            }

    private fun checkCardExpirationDate(cardExpirationMonth: String, cardExpirationYear: String): Boolean =
        cardInfoValidator
            .validateFutureDate(cardExpirationMonth, formatYear(cardExpirationYear))
            .also { valid ->
                if (!valid) view?.showErrorPopup(Resource(R.string.paymaya_card_expired_error))
            }

    override fun cardNumberChanged() {
        view?.hideCardNumberError()
    }

    override fun cardExpirationMonthChanged() {
        view?.hideCardExpirationMonthError()
    }

    override fun cardExpirationYearChanged() {
        view?.hideCardExpirationYearError()
    }

    override fun cardCvcChanged() {
        view?.hideCardCvcError()
    }

    override fun cardNumberFocusLost(value: String) {
        checkCardNumber(value)
    }

    override fun cardExpirationMonthFocusLost(value: String) {
        checkCardExpirationMonth(value)
    }

    override fun cardExpirationYearFocusLost(value: String) {
        checkCardExpirationYear(value)
    }

    override fun cardCvcFocusLost(value: String) {
        checkCardCvc(value)
    }

    private fun prepareCardModel(
        cardNumber: String,
        cardExpirationMonth: String,
        cardExpirationYear: String,
        cardCvc: String
    ): Card =
        Card(
            number = cardNumber,
            expMonth = cardExpirationMonth.padStart(length = 2, padChar = '0'),
            expYear = formatYear(cardExpirationYear),
            cvc = cardCvc
        )

    private fun formatYear(cardExpirationYear: String) =
        "$YEAR_PREFIX$cardExpirationYear"

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

    private fun getExceptionMessage(exception: Exception): Resource =
        when (exception) {
            is BadRequestException -> getMessageBadRequestMessage(exception.error)
            is UnknownHostException -> Resource(R.string.paymaya_connection_error)
            else -> Resource(R.string.paymaya_unknown_error)
        }

    private fun getMessageBadRequestMessage(baseError: BaseError): Resource =
        when (baseError) {
            is GenericError -> Resource(baseError.error)
            is PaymentError -> Resource(getPaymentErrorMessage(baseError))
            else -> Resource(R.string.paymaya_unknown_error)
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

    companion object {
        private const val YEAR_PREFIX = "20"
    }
}