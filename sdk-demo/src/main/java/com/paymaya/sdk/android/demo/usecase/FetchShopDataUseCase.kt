package com.paymaya.sdk.android.demo.usecase

import com.paymaya.sdk.android.demo.model.ItemAmount
import com.paymaya.sdk.android.demo.model.ShopProduct
import java.math.BigDecimal

class FetchShopDataUseCase {
    fun run(): List<ShopProduct> =
        listOf(
            ShopProduct(
                "Shoes",
                null,
                null,
                ItemAmount(BigDecimal.valueOf(99.0)),
                "PHP"
            ),
            ShopProduct(
                "Shirt",
                null,
                null,
                ItemAmount(BigDecimal.valueOf(20.0)),
                "PHP"
            ),
            ShopProduct(
                "Pants",
                null,
                null,
                ItemAmount(BigDecimal.valueOf(10.0)),
                "PHP"
            )
        )
}