package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.ui.cart.CartContract
import com.paymaya.sdk.android.demo.ui.cart.CartPresenter
import com.paymaya.sdk.android.demo.ui.shop.ShopContract
import com.paymaya.sdk.android.demo.ui.shop.ShopPresenter

internal object PresenterModule {

    fun getShopPresenter(): ShopContract.Presenter =
        ShopPresenter(
            UseCaseModule.getFetchShopDataUseCase(),
            UseCaseModule.getSaveProductInCartUseCase(),
            UseCaseModule.getFetchTotalCountFromCartUseCase()
        )

    fun getCartPresenter(): CartContract.Presenter =
        CartPresenter(
            UseCaseModule.getFetchProductsFromCartUseCase(),
            UseCaseModule.getFetchTotalAmountFromCartUseCase(),
            UseCaseModule.getRemoveProductFromCartUseCase(),
            UseCaseModule.getCreateCheckoutRequestUseCase(),
            UseCaseModule.getCreateSinglePaymentsRequestUseCase(),
            UseCaseModule.getCreateWalletLinkRequestUseCase()
        )
}
