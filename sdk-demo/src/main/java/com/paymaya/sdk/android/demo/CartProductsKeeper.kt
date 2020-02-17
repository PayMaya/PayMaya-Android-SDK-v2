package com.paymaya.sdk.android.demo

import com.paymaya.sdk.android.demo.model.ShopProduct

class CartProductsKeeper {
    private var productsInCart: MutableList<ShopProduct> = mutableListOf()

    fun addProduct(shopProduct: ShopProduct) {
        productsInCart.add(shopProduct)
    }

    fun removeProduct(shopProduct: ShopProduct) {
        productsInCart.remove(shopProduct)
    }

    fun fetchProducts(): List<ShopProduct> =
        productsInCart

}