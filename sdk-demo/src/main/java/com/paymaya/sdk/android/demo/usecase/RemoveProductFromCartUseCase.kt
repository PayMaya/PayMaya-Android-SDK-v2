package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsKeeper
import com.paymaya.sdk.android.demo.model.CartProduct

class RemoveProductFromCartUseCase(
    private val cartProductsKeeper: CartProductsKeeper
) {
    fun run(cartProduct: CartProduct) {
        cartProductsKeeper.removeProduct(cartProduct)
    }
}