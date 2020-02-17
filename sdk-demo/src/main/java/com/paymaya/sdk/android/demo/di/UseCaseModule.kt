package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.usecase.FetchProductsFromCartUseCase
import com.paymaya.sdk.android.demo.usecase.FetchShopDataUseCase
import com.paymaya.sdk.android.demo.usecase.SaveProductInCartUseCase

internal interface UseCaseModule {
    val fetchShopDataUseCase: FetchShopDataUseCase
    val saveProductInCartUseCase: SaveProductInCartUseCase
    val fetchProductsFromCartUseCase: FetchProductsFromCartUseCase
}

internal object UseCaseModuleProvider : UseCaseModule {
    override val fetchShopDataUseCase: FetchShopDataUseCase = FetchShopDataUseCase()
    override val saveProductInCartUseCase: SaveProductInCartUseCase = SaveProductInCartUseCase(
        DataKeeperModuleProvider.cartProductsKeeper
    )
    override val fetchProductsFromCartUseCase: FetchProductsFromCartUseCase = FetchProductsFromCartUseCase(
        DataKeeperModuleProvider.cartProductsKeeper
    )
}