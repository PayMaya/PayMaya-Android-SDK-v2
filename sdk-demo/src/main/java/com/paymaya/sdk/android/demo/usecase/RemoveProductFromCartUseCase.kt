package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsRepository
import com.paymaya.sdk.android.demo.model.CartItem

class RemoveProductFromCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(cartItem: CartItem) {
        cartProductsRepository.removeProduct(cartItem)
    }
}