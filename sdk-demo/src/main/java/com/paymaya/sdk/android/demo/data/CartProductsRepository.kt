package com.paymaya.sdk.android.demo.data

import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.model.ShopProduct
import java.math.BigDecimal

class CartProductsRepository {

    private var cartProducts: MutableList<CartProduct> = mutableListOf()

    fun addProduct(shopProduct: ShopProduct) {
        if (productExistInCart(shopProduct)) {
            val productIndex = productIndexInCart(shopProduct)
            updateCartProduct(productIndex, shopProduct.amount.value)
        } else {
            addNewCartProduct(shopProduct)
        }
    }

    private fun productExistInCart(shopProduct: ShopProduct): Boolean {
        cartProducts.forEach {
            if (it.name == shopProduct.name) return true
        }
        return false
    }

    private fun productIndexInCart(shopProduct: ShopProduct): Int =
        cartProducts.indexOfFirst { it.name == shopProduct.name }

    private fun updateCartProduct(productIndex: Int, amount: BigDecimal) {
        cartProducts[productIndex].apply {
            quantity++
            totalAmount += amount
        }
    }

    private fun addNewCartProduct(shopProduct: ShopProduct) {
        cartProducts.add(
            CartProduct(
                DEFAULT_PRODUCT_QUANTITY,
                shopProduct.amount.value,
                shopProduct.name,
                shopProduct.currency,
                shopProduct.amount,
                shopProduct.description,
                shopProduct.code
            )
        )
    }

    fun removeProduct(product: CartProduct) {
        val cartProduct = cartProducts.first { it.name == product.name }
        with(cartProduct) {
            totalAmount -= amount.value
            quantity -= 1
            if (quantity == 0) cartProducts.remove(this)
        }
    }

    fun fetchProducts(): List<CartProduct> =
        cartProducts

    companion object {
        private const val DEFAULT_PRODUCT_QUANTITY = 1
    }

}