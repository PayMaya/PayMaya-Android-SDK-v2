package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsRepository
import com.paymaya.sdk.android.demo.model.CartProduct

class FetchProductsFromCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(): List<CartProduct> =
        cartProductsRepository.fetchProducts()
}