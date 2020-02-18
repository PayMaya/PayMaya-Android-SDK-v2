package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.ui.cart.CartContract
import com.paymaya.sdk.android.demo.ui.cart.CartPresenter
import com.paymaya.sdk.android.demo.ui.shop.ShopContract
import com.paymaya.sdk.android.demo.ui.shop.ShopPresenter

internal interface PresenterModule {
    val shopPresenter: ShopContract.Presenter
    val cartPresenter: CartContract.Presenter
}

internal object PresenterModuleProvider : PresenterModule {
    override val shopPresenter: ShopContract.Presenter by lazy {
        ShopPresenter(
            UseCaseModuleProvider.fetchShopDataUseCase,
            UseCaseModuleProvider.saveProductInCartUseCase,
            UseCaseModuleProvider.fetchProductsFromCartUseCase
        )
    }
    override val cartPresenter: CartContract.Presenter by lazy {
        CartPresenter(
            UseCaseModuleProvider.fetchProductsFromCartUseCase,
            UseCaseModuleProvider.removeProductFromCartUseCase,
            MapperModuleProvider.checkoutPaymentMapper
        )
    }
}