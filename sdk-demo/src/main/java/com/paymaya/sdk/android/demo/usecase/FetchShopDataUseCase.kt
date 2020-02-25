package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.Constants.CURRENCY
import com.paymaya.sdk.android.demo.model.ShopItem
import java.math.BigDecimal

class FetchShopDataUseCase {
    fun run(): List<ShopItem> =
        listOf(
            ShopItem(
                name = "Shoes",
                currency = CURRENCY,
                value = BigDecimal(99.0),
                code = null,
                description = null,
                discount = BigDecimal(20)
            ),
            ShopItem(
                name = "Shirt",
                currency = CURRENCY,
                value = BigDecimal(19.99),
                code = null,
                description = null,
                discount = BigDecimal(10)
            ),
            ShopItem(
                name = "Pants",
                currency = CURRENCY,
                value = BigDecimal(20.9),
                code = null,
                description = null,
                discount = BigDecimal(2)
            )
        )
}