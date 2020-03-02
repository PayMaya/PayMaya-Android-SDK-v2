package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.data.CartProductsRepository
import java.math.BigDecimal

class FetchTotalAmountFromCartUseCase(
    private val cartProductsRepository: CartProductsRepository
) {
    fun run(): BigDecimal =
        cartProductsRepository.getTotalAmount()
}