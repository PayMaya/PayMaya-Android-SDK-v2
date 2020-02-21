package com.paymaya.sdk.android.demo.data

import com.paymaya.sdk.android.demo.model.CartItem
import com.paymaya.sdk.android.demo.model.ShopItem

class CartProductsRepository {

    private var cartItems: MutableList<CartItem> = mutableListOf()

    fun addProduct(shopItem: ShopItem) {
        val product = cartItems.firstOrNull { it.name == shopItem.name }
        if (product == null) {
            addNewCartProduct(shopItem)
        } else {
            updateCartProduct(product)
        }
    }

    private fun updateCartProduct(product: CartItem) {
        product.apply {
            quantity++
            totalAmount += this.amount.value
        }
    }

    private fun addNewCartProduct(shopItem: ShopItem) {
        cartItems.add(
            CartItem(
                quantity = 1,
                totalAmount = shopItem.amount.value,
                name = shopItem.name,
                currency = shopItem.currency,
                amount = shopItem.amount,
                description = shopItem.description,
                code = shopItem.code
            )
        )
    }

    fun removeProduct(product: CartItem) {
        val cartProduct = cartItems.first { it.name == product.name }
        with(cartProduct) {
            totalAmount -= amount.value
            quantity--
            if (quantity == 0) cartItems.remove(this)
        }
    }

    fun fetchProducts(): List<CartItem> =
        cartItems

}