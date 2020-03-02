package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.checkout.models.Buyer
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.Constants
import com.paymaya.sdk.android.demo.data.CartProductsRepository

class CreateCheckoutRequestUseCase(
    private val repository: CartProductsRepository
) {

    fun run(): CheckoutRequest? {
        val products = repository.getItems()

        return if (products.isNotEmpty())
            CheckoutRequest(
                getTotalAmount(),
                getBuyerDetails(),
                getItems(products),
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
                    Constants.CURRENCY,
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
            success = Constants.REDIRECT_URL_SUCCESS,
            failure = Constants.REDIRECT_URL_FAILURE,
            cancel = Constants.REDIRECT_URL_CANCEL
        )
}