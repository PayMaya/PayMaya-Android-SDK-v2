package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsKeeper
import com.paymaya.sdk.android.demo.model.ShopProduct

class SaveProductInCartUseCase(
    private val cartProductsKeeper: CartProductsKeeper
) {
    fun run(shopProduct: ShopProduct) {
        cartProductsKeeper.addProduct(shopProduct)
    }
}