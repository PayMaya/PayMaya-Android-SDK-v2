package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsRepository
import com.paymaya.sdk.android.demo.model.CartItem

class FetchProductsFromCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(): List<CartItem> =
        cartProductsRepository.fetchProducts()
}