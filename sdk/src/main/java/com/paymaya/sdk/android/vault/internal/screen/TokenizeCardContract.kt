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

import androidx.annotation.DrawableRes
import com.paymaya.sdk.android.common.internal.AndroidString
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse

internal interface TokenizeCardContract {
    interface View {
        fun finishSuccess(tokenizeCardResponse: TokenizeCardResponse)
        fun finishCanceled()

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
        fun showErrorPopup(message: AndroidString)
    }

    interface Presenter {
        fun viewCreated(view: View)
        fun viewDestroyed()

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
        fun backButtonPressed()
    }
}
