package com.paymaya.sdk.android.demo.mapper

import com.paymaya.sdk.android.checkout.models.*
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.model.CartProduct
import java.math.BigDecimal

class CheckoutPaymentMapper {

    fun run(cartProducts: List<CartProduct>): CheckoutRequest? {
        return if (cartProducts.isNotEmpty())
            CheckoutRequest(
                getTotalAmount(cartProducts),
                getBuyerDetails(),
                getItemsList(cartProducts),
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

    private fun getItemAmount(product: CartProduct): ItemAmount? {
        val amount = getSingleAmount(product)
        val amountDetails = getAmountDetails()

        return if (amount != null && amountDetails != null) {
            ItemAmount(amount, amountDetails)
        } else null
    }

    private fun getSingleAmount(product: CartProduct): BigDecimal? =
        product.items.first().amount?.value


    private fun getAmountDetails(): AmountDetails? {
        return AmountDetails()
    }

    private fun getCurrency(products: List<CartProduct>): String {
        val cartProduct = products.first()
        val singleCartItem = cartProduct.items.first()
        return singleCartItem.currency
    }

    private fun getTotalAmountValue(products: List<CartProduct>): BigDecimal {
        var totalAmount = BigDecimal(0)
        products.forEach {
            totalAmount += it.totalAmount
        }
        return totalAmount
    }

    private fun List<CartProduct>.toItemsList(): List<Item> {
        return map {
            Item(
                it.name,
                it.items.size,
                it.code,
                it.description,
                getItemAmount(it),
                getTotalAmount(this)
            )
        }
    }

}