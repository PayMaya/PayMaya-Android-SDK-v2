package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsRepository
import com.paymaya.sdk.android.demo.model.CartProduct

class RemoveProductFromCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(cartProduct: CartProduct) {
        cartProductsRepository.removeProduct(cartProduct)
    }
}