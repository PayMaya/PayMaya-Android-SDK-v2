package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.demo.data.CartProductsRepository

class RemoveProductFromCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(cartItem: Item) {
        cartProductsRepository.removeItem(cartItem)
    }
}