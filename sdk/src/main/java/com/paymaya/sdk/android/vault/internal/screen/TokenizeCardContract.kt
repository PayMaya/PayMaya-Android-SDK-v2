package com.paymaya.sdk.android.vault.internal.screen

import com.paymaya.sdk.android.common.internal.Resource
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse

internal interface TokenizeCardContract {
    interface View {
        fun finishSuccess(tokenizeCardResponse: TokenizeCardResponse)
        fun showCardNumberError()
        fun hideCardNumberError()
        fun hideCardExpirationYearError()
        fun showCardExpirationYearError()
        fun hideCardExpirationMonthError()
        fun showCardExpirationMonthError()
        fun hideCardCvcError()
        fun showCardCvcError()
        fun finishCanceled()
        fun showErrorPopup(message: Resource)
        fun showProgressBar()
        fun hideProgressBar()
        fun hideKeyboard()
    }

    interface Presenter {
        fun viewCreated(view: View)
        fun viewDestroyed()
        fun backButtonPressed()
        fun cardNumberChanged()
        fun cardExpirationMonthChanged()
        fun cardExpirationYearChanged()
        fun cardCvcChanged()
        fun cardNumberFocusLost(value: String)
        fun cardExpirationMonthFocusLost(value: String)
        fun cardExpirationYearFocusLost(value: String)
        fun cardCvcFocusLost(value: String)
        fun payButtonClicked(
            cardNumber: String,
            cardExpirationMonth: String,
            cardExpirationYear: String,
            cardCvc: String
        )
    }
}
