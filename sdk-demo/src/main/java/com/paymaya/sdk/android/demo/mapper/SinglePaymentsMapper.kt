package com.paymaya.sdk.android.demo.mapper

import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.usecase.FetchProductsFromCartUseCase
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import java.math.BigDecimal

class SinglePaymentsMapper(
    private val fetchProductsFromCartUseCase: FetchProductsFromCartUseCase
) {
    fun run(): SinglePaymentRequest? {
        val products = fetchProductsFromCartUseCase.run()

        return if (products.isNotEmpty())
            SinglePaymentRequest(
                getTotalAmount(products),
                getRequestReferenceNumber(),
                getRedirectUrl()
            ) else null
    }

    private fun getTotalAmount(products: List<CartProduct>): TotalAmount =
        TotalAmount(
            getTotalAmountValue(products),
            getCurrency(products),
            getAmountDetails()
        )

    private fun getRequestReferenceNumber(): String {
        return "REQ_2"
    }

    private fun getRedirectUrl(): RedirectUrl =
        RedirectUrl(
            success = "http://success.com",
            failure = "http://failure.com",
            cancel = "http://cancel.com"
        )

    private fun getAmountDetails(): AmountDetails? {
        return AmountDetails()
    }

    private fun getCurrency(products: List<CartProduct>): String {
        val cartProduct = products.first()
        return cartProduct.currency
    }

    private fun getTotalAmountValue(products: List<CartProduct>): BigDecimal {
        var totalAmount = BigDecimal(0)
        products.forEach {
            totalAmount += it.totalAmount
        }
        return totalAmount
    }
}