package com.paymaya.sdk.android.vault.internal.screen

import androidx.annotation.DrawableRes
import com.paymaya.sdk.android.common.internal.Resource
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse

internal interface TokenizeCardContract {
    interface View {
        fun finishSuccess(tokenizeCardResponse: TokenizeCardResponse)
        fun showCardNumberError()
        fun hideCardNumberError()
        fun showCardExpirationDateError()
        fun hideCardExpirationDateError()
        fun showCardCvcError()
        fun hideCardCvcError()
        fun showCardIcon(@DrawableRes iconRes: Int)
        fun hideCardIcon()
        fun showCardCvcHint()
        fun hideCardCvcHint()
        fun showCardExpirationDateHint()
        fun showProgressBar()
        fun hideProgressBar()
        fun hideKeyboard()
        fun showErrorPopup(message: Resource)
        fun finishCanceled()
    }

    interface Presenter {
        fun viewCreated(view: View)
        fun viewDestroyed()
        fun backButtonPressed()
        fun cardNumberChanged(cardNumber: String)
        fun cardExpirationDateChanged()
        fun cardCvcChanged()
        fun cardNumberFocusLost(value: String)
        fun cardExpirationDateFocusReceived()
        fun cardExpirationDateFocusLost(value: String)
        fun cardCvcFocusLost(value: String)
        fun payButtonClicked(
            cardNumberWithSpaces: String,
            cardExpirationDate: String,
            cardCvc: String
        )
        fun screenMaskClicked()
        fun cardCvcInfoClicked()
    }
}
