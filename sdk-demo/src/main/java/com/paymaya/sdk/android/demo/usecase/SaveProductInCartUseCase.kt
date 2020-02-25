package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsRepository
import com.paymaya.sdk.android.demo.model.ShopItem

class SaveProductInCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(shopItem: ShopItem) {
        cartProductsRepository.addItem(shopItem)
    }
}