package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.Constants.CURRENCY
import com.paymaya.sdk.android.demo.data.CartProductsRepository
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import java.math.BigDecimal

class CreateSinglePaymentsRequestUseCase(
    private val repository: CartProductsRepository
) {
    fun run(): SinglePaymentRequest? {
        val products = repository.fetchItems()

        return if (products.isNotEmpty())
            SinglePaymentRequest(
                getTotalAmounts(products),
                getRequestReferenceNumber(),
                getRedirectUrl()
            ) else null
    }

    private fun getTotalAmounts(products: List<Item>): TotalAmount =
        TotalAmount(
            getProductsAmountValue(products),
            CURRENCY,
            AmountDetails()
        )

    private fun getProductsAmountValue(products: List<Item>): BigDecimal {
        var totalAmount = BigDecimal(0)
        products.forEach {
            totalAmount += it.totalAmount.value
        }
        return totalAmount
    }

    private fun getRequestReferenceNumber(): String {
        return "REQ_2"
    }

    private fun getRedirectUrl(): RedirectUrl =
        RedirectUrl(
            success = "http://success.com",
            failure = "http://failure.com",
            cancel = "http://cancel.com"
        )
}