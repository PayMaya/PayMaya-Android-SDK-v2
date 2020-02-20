package com.paymaya.sdk.android.demo.data

import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.model.ShopProduct
import java.math.BigDecimal

class CartProductsRepository {

    private var cartProducts: MutableList<CartProduct> = mutableListOf()

    fun addProduct(shopProduct: ShopProduct) {
        val product = cartProducts.firstOrNull { it.name == shopProduct.name }
        if (product == null) {
            addNewCartProduct(shopProduct)
        } else {
            updateCartProduct(product)
        }
    }

    private fun updateCartProduct(product: CartProduct) {
        product.apply {
            quantity++
            totalAmount += this.amount.value
        }
    }

    private fun addNewCartProduct(shopProduct: ShopProduct) {
        cartProducts.add(
            CartProduct(
                quantity = 1,
                totalAmount = shopProduct.amount.value,
                name = shopProduct.name,
                currency = shopProduct.currency,
                amount = shopProduct.amount,
                description = shopProduct.description,
                code = shopProduct.code
            )
        )
    }

    fun removeProduct(product: CartProduct) {
        val cartProduct = cartProducts.first { it.name == product.name }
        with(cartProduct) {
            totalAmount -= amount.value
            quantity --
            if (quantity == 0) cartProducts.remove(this)
        }
    }

    fun fetchProducts(): List<CartProduct> =
        cartProducts

}