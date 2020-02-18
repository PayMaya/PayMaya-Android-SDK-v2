package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.mapper.CheckoutPaymentMapper

internal interface MapperModule {
    val checkoutPaymentMapper: CheckoutPaymentMapper
}

internal object MapperModuleProvider : MapperModule{
    override val checkoutPaymentMapper: CheckoutPaymentMapper by lazy { CheckoutPaymentMapper() }
}