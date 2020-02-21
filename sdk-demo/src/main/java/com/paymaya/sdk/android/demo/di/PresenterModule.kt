package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.ui.cart.CartContract
import com.paymaya.sdk.android.demo.ui.cart.CartPresenter
import com.paymaya.sdk.android.demo.ui.shop.ShopContract
import com.paymaya.sdk.android.demo.ui.shop.ShopPresenter

internal object PresenterModuleProvider {

    fun getShopPresenter(): ShopContract.Presenter =
        ShopPresenter(
            UseCaseModuleProvider.getFetchShopDataUseCase(),
            UseCaseModuleProvider.getSaveProductInCartUseCase(),
            UseCaseModuleProvider.getFetchProductsFromCartUseCase()
        )

    fun getCartPresenter(): CartContract.Presenter =
        CartPresenter(
            UseCaseModuleProvider.getFetchProductsFromCartUseCase(),
            UseCaseModuleProvider.getRemoveProductFromCartUseCase(),
            UseCaseModuleProvider.getCreateCheckoutRequestUseCase(),
            UseCaseModuleProvider.getCreateSinglePaymentsRequestUseCase(),
            UseCaseModuleProvider.getCreateWalletLinkRequestUseCase()
        )
}
