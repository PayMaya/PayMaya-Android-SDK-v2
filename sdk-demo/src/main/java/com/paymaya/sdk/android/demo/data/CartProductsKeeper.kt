package com.paymaya.sdk.android.demo.data

import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.model.ShopProduct
import java.math.BigDecimal

class CartProductsKeeper {
    private var productsShopList: MutableList<ShopProduct> = mutableListOf()
    private var productsInCart: MutableList<CartProduct> = mutableListOf()
    private var productsSortedByName: Map<String, List<ShopProduct>> = mapOf()

    fun addProduct(shopProduct: ShopProduct) {
        productsShopList.add(shopProduct)
        productsSortedByName = productsShopList.groupBy { it.name }
    }

    fun removeProduct(cartProduct: CartProduct) {
        productsShopList.remove(cartProduct.items.first())
        productsSortedByName = productsShopList.groupBy { it.name }
    }

    fun fetchProducts(): List<CartProduct> {
        productsInCart.clear()

        for ((key, value) in productsSortedByName) {

            val totalAmount: BigDecimal? = getTotalAmount(value)

            val productCode = value.first().code
            val productDescription = value.first().description
            val productCurrency = value.first().currency

            requireNotNull(totalAmount)
            productsInCart.add(
                CartProduct(key, value, totalAmount, productCode, productDescription, productCurrency)
            )
        }
        return productsInCart
    }

    private fun getTotalAmount(products: List<ShopProduct>): BigDecimal? {
        val productsCount = products.size
        val singleProductAmount = products.first().amount?.value
        return singleProductAmount?.let { it * productsCount.toBigDecimal() }
    }

}