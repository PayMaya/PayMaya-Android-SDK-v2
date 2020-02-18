package com.paymaya.sdk.android.demo.mapper

import com.paymaya.sdk.android.checkout.models.*
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.usecase.FetchProductsFromCartUseCase
import java.math.BigDecimal

class CheckoutPaymentMapper(
    private val fetchProductsFromCartUseCase: FetchProductsFromCartUseCase
) {

    fun run(): CheckoutRequest? {
        val products = fetchProductsFromCartUseCase.run()

        return if (products.isNotEmpty())
            CheckoutRequest(
                getProductsTotalAmounts(products),
                getBuyerDetails(),
                getItemsList(products),
                getRequestReferenceNumber(),
                getRedirectUrl()
            ) else null
    }

    private fun getProductsTotalAmounts(products: List<CartProduct>): TotalAmount =
        TotalAmount(
            getProductsAmountValue(products),
            getCurrency(products.first()),
            getAmountDetails()
        )

    private fun getSingleProductTotalAmount(product: CartProduct): TotalAmount =
        TotalAmount(
            getSingleProductAmountValue(product),
            getCurrency(product),
            getAmountDetails()
        )

    private fun getProductsAmountValue(products: List<CartProduct>): BigDecimal {
        var totalAmount = BigDecimal(0)
        products.forEach {
            totalAmount += it.totalAmount
        }
        return totalAmount
    }

    private fun getSingleProductAmountValue(product: CartProduct): BigDecimal {
        var totalAmount = BigDecimal(0)
        product.items.forEach {
            it.amount?.value?.let { value -> totalAmount += value }
        }
        return totalAmount
    }


    private fun getBuyerDetails(): Buyer {
        return Buyer(
            firstName = "John",
            middleName = "Thomas",
            lastName = "Smith",
            contact = null,
            shippingAddress = null,
            billingAddress = null,
            ipAddress = null
        )
    }

    private fun getItemsList(products: List<CartProduct>): List<Item> =
        products.toItemsList()


    private fun getRequestReferenceNumber(): String {
        return "REQ_1"
    }

    private fun getRedirectUrl(): RedirectUrl =
        RedirectUrl(
            success = "http://success.com",
            failure = "http://failure.com",
            cancel = "http://cancel.com"
        )

    private fun getSingleItemAmount(product: CartProduct): ItemAmount? {
        val amount =  product.items.firstOrNull()?.amount?.value
        val amountDetails = getAmountDetails()

        return if (amount != null && amountDetails != null) {
            ItemAmount(amount, amountDetails)
        } else null
    }

    private fun getAmountDetails(): AmountDetails? {
        return AmountDetails()
    }

    private fun getCurrency(product: CartProduct): String {
        val singleCartItem = product.items.first()
        return singleCartItem.currency
    }

    private fun List<CartProduct>.toItemsList(): List<Item> {
        return map {
            Item(
                it.name,
                it.items.size,
                it.code,
                it.description,
                getSingleItemAmount(it),
                getSingleProductTotalAmount(it)
            )
        }
    }

}