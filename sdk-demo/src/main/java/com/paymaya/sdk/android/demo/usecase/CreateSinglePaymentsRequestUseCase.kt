package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.Constants
import com.paymaya.sdk.android.demo.data.CartProductsRepository
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest

class CreateSinglePaymentsRequestUseCase(
    private val repository: CartProductsRepository
) {
    fun run(): SinglePaymentRequest? {
        val products = repository.getItems()

        return if (products.isNotEmpty())
            SinglePaymentRequest(
                getTotalAmount(),
                getRequestReferenceNumber(),
                getRedirectUrl()
            ) else null
    }

    private fun getTotalAmount(): TotalAmount =
        TotalAmount(
            repository.getTotalAmount(),
            Constants.CURRENCY,
            AmountDetails()
        )

    private fun getRequestReferenceNumber(): String {
        return "REQ_2"
    }

    private fun getRedirectUrl(): RedirectUrl =
        RedirectUrl(
            success = Constants.REDIRECT_URL_SUCCESS,
            failure = Constants.REDIRECT_URL_FAILURE,
            cancel = Constants.REDIRECT_URL_CANCEL
        )
}