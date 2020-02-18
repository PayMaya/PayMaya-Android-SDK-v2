package com.paymaya.sdk.android.demo.model

import java.math.BigDecimal

class ProductDetail {

    data class ItemAmount(
        val value: BigDecimal,
        val details: AmountDetails? = null
    )

    data class TotalAmount(
        val value: BigDecimal,
        val currency: String,
        val details: AmountDetails? = null
    )

    class AmountDetails(
        val discount: BigDecimal? = null,
        val serviceCharge: BigDecimal? = null,
        val shippingFee: BigDecimal? = null,
        val tax: BigDecimal? = null,
        val subtotal: BigDecimal? = null
    )
}