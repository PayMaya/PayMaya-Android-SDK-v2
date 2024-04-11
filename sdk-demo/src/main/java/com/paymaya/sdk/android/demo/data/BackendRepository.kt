package com.paymaya.sdk.android.demo.data

import com.paymaya.sdk.android.demo.Constants
import com.paymaya.sdk.android.demo.model.ShopItem
import java.math.BigDecimal
import java.math.RoundingMode

class BackendRepository {
    fun getShopItems(): List<ShopItem> =
        listOf(
            ShopItem(
                name = "Shoes",
                currency = Constants.CURRENCY,
                value = toAmount(99.0),
                code = null,
                description = null,
                discount = toAmount(20)
            ),
            ShopItem(
                name = "Shirt",
                currency = Constants.CURRENCY,
                value = toAmount(19.99),
                code = null,
                description = null,
                discount = toAmount(10)
            ),
            ShopItem(
                name = "Pants",
                currency = Constants.CURRENCY,
                value = toAmount(20.9),
                code = null,
                description = null,
                discount = toAmount(2)
            )
        )

    companion object {
        fun toAmount(amount: Double): BigDecimal = toAmount(BigDecimal(amount))

        fun toAmount(amount: Int): BigDecimal = toAmount(BigDecimal(amount))

        private fun toAmount(amount: BigDecimal): BigDecimal =
            amount.setScale(Constants.DECIMALS, RoundingMode.HALF_DOWN)
    }
}