package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.checkout.models.Buyer
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.Constants.CURRENCY
import com.paymaya.sdk.android.demo.data.CartProductsRepository
import java.math.BigDecimal

class CreateCheckoutRequestUseCase(
    private val repository: CartProductsRepository
) {

    fun run(): CheckoutRequest? {
        val products = repository.fetchItems()

        return if (products.isNotEmpty())
            CheckoutRequest(
                getTotalAmounts(products),
                getBuyerDetails(),
                getItems(products),
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

    private fun getItems(products: List<Item>): List<Item> =
        products.map {
            Item(
                it.name,
                it.quantity,
                it.code,
                it.description,
                it.amount,
                TotalAmount(
                    it.totalAmount.value,
                    CURRENCY,
                    it.amount?.details
                )
            )
        }

    private fun getBuyerDetails(): Buyer =
        Buyer(
            firstName = "John",
            middleName = "Thomas",
            lastName = "Smith",
            contact = null,
            shippingAddress = null,
            billingAddress = null,
            ipAddress = null
        )

    private fun getRequestReferenceNumber(): String {
        return "REQ_1"
    }

    private fun getRedirectUrl(): RedirectUrl =
        RedirectUrl(
            success = "http://success.com",
            failure = "http://failure.com",
            cancel = "http://cancel.com"
        )
}