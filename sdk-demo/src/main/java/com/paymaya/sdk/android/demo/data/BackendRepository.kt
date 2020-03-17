package com.paymaya.sdk.android.demo.data

import com.paymaya.sdk.android.demo.Constants
import com.paymaya.sdk.android.demo.model.ShopItem
import java.math.BigDecimal

class BackendRepository {
    fun getShopItems(): List<ShopItem> =
        listOf(
            ShopItem(
                name = "Shoes",
                currency = Constants.CURRENCY,
                value = BigDecimal(99.0),
                code = null,
                description = null,
                discount = BigDecimal(20)
            ),
            ShopItem(
                name = "Shirt",
                currency = Constants.CURRENCY,
                value = BigDecimal(19.99),
                code = null,
                description = null,
                discount = BigDecimal(10)
            ),
            ShopItem(
                name = "Pants",
                currency = Constants.CURRENCY,
                value = BigDecimal(20.9),
                code = null,
                description = null,
                discount = BigDecimal(2)
            )
        )
}