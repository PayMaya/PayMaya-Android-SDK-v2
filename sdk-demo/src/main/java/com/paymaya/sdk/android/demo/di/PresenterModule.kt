package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.ui.shop.ShopContract
import com.paymaya.sdk.android.demo.ui.shop.ShopPresenter

internal interface PresenterModule {
    val shopPresenter: ShopContract.Presenter
}

internal object PresenterModuleProvider : PresenterModule {
    override val shopPresenter: ShopContract.Presenter by lazy {
        ShopPresenter(
            UseCaseModuleProvider.fetchShopDataUseCase,
            UseCaseModuleProvider.saveProductInCartUseCase,
            UseCaseModuleProvider.fetchProductsFromCartUseCase
        )
    }
}