package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.demo.data.CartProductsRepository

class FetchProductsFromCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(): List<Item> =
        cartProductsRepository.fetchItems()
}