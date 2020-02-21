package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.checkout.models.ItemAmount
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.demo.Constants.CURRENCY
import com.paymaya.sdk.android.demo.model.ShopItem
import java.math.BigDecimal

class FetchShopDataUseCase {
    fun run(): List<ShopItem> =
        listOf(
            ShopItem(
                name = "Shoes",
                code = null,
                description = null,
                amount = ItemAmount(BigDecimal(99.0), AmountDetails()),
                currency = CURRENCY
            ),
            ShopItem(
                name = "Shirt",
                code = null,
                description = null,
                amount = ItemAmount(BigDecimal.valueOf(20.0)),
                currency = CURRENCY
            ),
            ShopItem(
                name = "Pants",
                code = null,
                description = null,
                amount = ItemAmount(BigDecimal.valueOf(10.0)),
                currency = CURRENCY
            )
        )
}