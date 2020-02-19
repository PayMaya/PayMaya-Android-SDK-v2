package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsRepository
import com.paymaya.sdk.android.demo.model.ShopProduct

class SaveProductInCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(shopProduct: ShopProduct) {
        cartProductsRepository.addProduct(shopProduct)
    }
}