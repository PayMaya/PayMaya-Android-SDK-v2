package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsRepository

class FetchTotalCountFromCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(): Int =
        cartProductsRepository.getTotalCount()
}