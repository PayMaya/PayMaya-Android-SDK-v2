package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.mapper.CheckoutPaymentMapper
import com.paymaya.sdk.android.demo.mapper.SinglePaymentsMapper
import com.paymaya.sdk.android.demo.mapper.WalletLinkMapper

internal interface MapperModule {
    val checkoutPaymentMapper: CheckoutPaymentMapper
    val singlePaymentMapper: SinglePaymentsMapper
    val walletLinkMapper: WalletLinkMapper
}

internal object MapperModuleProvider : MapperModule {
    override val checkoutPaymentMapper: CheckoutPaymentMapper by lazy {
        CheckoutPaymentMapper(
            UseCaseModuleProvider.fetchProductsFromCartUseCase
        )
    }
    override val singlePaymentMapper: SinglePaymentsMapper by lazy {
        SinglePaymentsMapper(
            UseCaseModuleProvider.fetchProductsFromCartUseCase
        )
    }
    override val walletLinkMapper: WalletLinkMapper by lazy {
        WalletLinkMapper()
    }
}