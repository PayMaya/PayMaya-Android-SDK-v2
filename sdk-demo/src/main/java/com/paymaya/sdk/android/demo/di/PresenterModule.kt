package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.ui.cart.CartContract
import com.paymaya.sdk.android.demo.ui.cart.CartPresenter
import com.paymaya.sdk.android.demo.ui.shop.ShopContract
import com.paymaya.sdk.android.demo.ui.shop.ShopPresenter

internal object PresenterModule {

    fun getShopPresenter(): ShopContract.Presenter =
        ShopPresenter(
            RepositoryModule.backendRepository,
            RepositoryModule.cartRepository
        )

    fun getCartPresenter(): CartContract.Presenter =
        CartPresenter(
            RepositoryModule.cartRepository
        )
}
