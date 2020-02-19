package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.ui.cart.CartContract
import com.paymaya.sdk.android.demo.ui.cart.CartPresenter
import com.paymaya.sdk.android.demo.ui.shop.ShopContract
import com.paymaya.sdk.android.demo.ui.shop.ShopPresenter

internal object PresenterModuleProvider {
    val shopPresenter: ShopContract.Presenter by lazy {
        ShopPresenter(
            UseCaseModuleProvider.fetchShopDataUseCase,
            UseCaseModuleProvider.saveProductInCartUseCase,
            UseCaseModuleProvider.fetchProductsFromCartUseCase
        )
    }
    val cartPresenter: CartContract.Presenter by lazy {
        CartPresenter(
            UseCaseModuleProvider.fetchProductsFromCartUseCase,
            UseCaseModuleProvider.removeProductFromCartUseCase,
            UseCaseModuleProvider.createCheckoutRequestUseCase,
            UseCaseModuleProvider.createSinglePaymentsRequestUseCase,
            UseCaseModuleProvider.createWalletLinkRequestUseCase
        )
    }
}