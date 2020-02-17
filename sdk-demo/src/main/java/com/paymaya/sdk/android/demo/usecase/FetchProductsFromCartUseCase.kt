package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.CartProductsKeeper
import com.paymaya.sdk.android.demo.model.ShopProduct

class FetchProductsFromCartUseCase(
    private val cartProductsKeeper: CartProductsKeeper
) {
    fun run(): List<ShopProduct> =
        cartProductsKeeper.fetchProducts()

}